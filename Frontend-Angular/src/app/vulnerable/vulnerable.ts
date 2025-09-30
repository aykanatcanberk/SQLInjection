import { Component, ChangeDetectorRef } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  standalone: true,
  imports: [CommonModule, FormsModule],
  selector: 'app-vulnerable',
  templateUrl: './vulnerable.html',
  styleUrls: ['./vulnerable.css']
})
export class Vulnerable {
  email: string = '';
  password: string = '';
  responseMsg: string | null = null;
  statusCode: number | null = null;

  private readonly LOGIN_URL = 'http://localhost:8081/api/vulnerable/login';

  constructor(private http: HttpClient, private cdr: ChangeDetectorRef) {}

  login(): void {
    this.responseMsg = null;
    this.statusCode = null;

    const payload = {
      email: this.email,
      password: this.password
    };

    this.http.post<any>(this.LOGIN_URL, payload, { observe: 'response' })
      .subscribe({
        next: (res: HttpResponse<any>) => {
          this.statusCode = res.status;
          this.responseMsg = JSON.stringify(res.body, null, 2);
          this.cdr.detectChanges();
        },
        error: (err: HttpErrorResponse) => {
          this.statusCode = err.status || 0;
          if (err.error) {
            try {
              this.responseMsg = JSON.stringify(err.error, null, 2);
            } catch {
              this.responseMsg = String(err.error);
            }
          } else {
            this.responseMsg = err.message;
          }
          this.cdr.detectChanges(); 
        }
      });
  }
}
