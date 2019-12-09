import {Component, OnInit} from '@angular/core';
import {ToolsService} from '../core/services/tools.service';

@Component({
  selector: 'app-tools',
  templateUrl: './tools.component.html',
  styleUrls: ['./tools.component.scss']
})
export class ToolsComponent implements OnInit {

  constructor(
    private toolsService: ToolsService
  ) {}
  loading = true;
  encryptOutput = '';
  decryptOutput = '';

  ngOnInit() {
    this.loading = false;
  }

  encrypt(toEncrypt: string): void {
    this.toolsService.encrypt(toEncrypt).subscribe(encryptedMap => {
      this.encryptOutput = encryptedMap['value'];
    });
  }

  decrypt(toDecrypt: string): void {
    this.toolsService.decrypt(toDecrypt).subscribe(decryptedMap => {
      this.decryptOutput = decryptedMap['value'];
    });
  }

}
