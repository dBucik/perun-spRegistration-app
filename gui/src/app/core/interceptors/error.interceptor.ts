import {Injectable} from '@angular/core';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from "@angular/common/http";
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import {DialogService} from "../../shared/dialog.service";
import {TranslateService} from "@ngx-translate/core";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private translate : TranslateService,
    private dialogService : DialogService
  ) { }

  private server_not_live_error: String = "asdf";

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        let errors = [];
        if (typeof err.error === "string") {
          errors.push(JSON.parse(err.error).errors);
        } else {
          errors.push(err.message);
        }

        if (errors === undefined || errors.length === 0) {
          errors.push(err.statusText);
        }

        if (err.status === 0) {
          errors.push(this.server_not_live_error);
        }

        this.dialogService.openDialog(errors);

        return throwError(err);
      }));
  }
}
