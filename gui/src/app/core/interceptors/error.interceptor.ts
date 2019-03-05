import {Injectable} from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from "@angular/common/http";
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import {DialogService} from "../../shared/dialog.service";
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private translate : TranslateService,
    private dialogService : DialogService,
    private router : Router
  ) {
    this.translate
      .get('ERROR.SERVER_DOWN')
      .subscribe(value => this.serverNotLiveError = value);
  }

  private serverNotLiveError: String;

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        let errors = [];

        if (err.status === 404) {
          this.router.navigate(['/notFound']);
          return;
        }
        if (err.status === 0) {
          errors.push(this.serverNotLiveError);
        }

        else if (typeof err.error === "string") {
          errors.push(JSON.parse(err.error).errors);
        } else {
          errors.push(err.message);
        }

        this.dialogService.openErrorDialog(errors);

        return throwError(err);
      }));
  }
}
