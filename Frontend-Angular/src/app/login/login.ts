import { Component, ChangeDetectorRef } from '@angular/core';
import { Api } from '../service/api';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule, RouterLink, RouterLinkActive, FormsModule],
  templateUrl: './login.html',
})
export class Login {

  readonly MAX_EMAIL_LENGTH = 254; 
  readonly MIN_PASSWORD_LENGTH = 8;
  readonly MAX_PASSWORD_LENGTH = 128;

  formData: any = {
    email: '',
    password: ''
  };

  error: any = null;
  submitting: boolean = false;
  passwordVisible: boolean = false; 

  private suspiciousPatterns: RegExp[] = [
    /('|--|;|\/\*|\*\/|#)/i,
    /\b(union|select|insert|update|delete|drop|alter|create|exec|execute)\b/i,
    /(\bor\b|\band\b).*(=|like)/i,
    /(%00|\\x00|\x00)/i,           
    /[\x00-\x1F\x7F]/             
  ];

  constructor(
    private apiService: Api,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  togglePasswordVisibility() {
    this.passwordVisible = !this.passwordVisible;
  }

  private trimInputs() {
    this.formData.email = (this.formData.email || '').toString().trim();
    this.formData.password = (this.formData.password || '').toString().trim();
  }

  private isSuspiciousInput(value: string): boolean {
    if (!value) return false;
    for (const p of this.suspiciousPatterns) {
      if (p.test(value)) return true;
    }
    return false;
  }

  private basicClientValidation(): string | null {
    if (!this.formData.email || !this.formData.password) {
      return 'Please fill all the fields correctly';
    }
    if (this.formData.email.length > this.MAX_EMAIL_LENGTH) {
      return 'Email is too long';
    }
    if (this.formData.password.length < this.MIN_PASSWORD_LENGTH) {
      return 'Password is too short';
    }
    if (this.formData.password.length > this.MAX_PASSWORD_LENGTH) {
      return 'Password is too long';
    }
    const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRe.test(this.formData.email)) {
      return 'Please enter a valid email address';
    }
    if (this.isSuspiciousInput(this.formData.email) || this.isSuspiciousInput(this.formData.password)) {
      return 'Input contains suspicious characters or patterns';
    }
    return null;
  }

  private sanitizeForTransport(value: string): string {
    if (!value) return value;
    return value.replace(/\s+/g, ' ');
  }

  async handleSubmit() {
    console.log("handle submit is called for login");

    this.trimInputs();

    const validationError = this.basicClientValidation();
    if (validationError) {
      this.showError(validationError);
      this.submitting = false;
      this.cdr.detectChanges();
      return;
    }

    this.submitting = true;
    this.cdr.detectChanges(); 

    const payload = {
      email: this.sanitizeForTransport(this.formData.email),
      password: this.sanitizeForTransport(this.formData.password)
    };

    this.apiService.loginUser(payload).subscribe({
      next: (res: any) => {
        this.submitting = false;
        this.cdr.detectChanges();

        if (res.status === 200) {
          this.apiService.encryptAndSaveToStorage('token', res.token);
          this.apiService.encryptAndSaveToStorage('role', res.role);
          this.router.navigate(['/home']);
        } else {
          this.showError(res?.message || 'Unable to login');
        }
      },
      error: (err: any) => {
        this.submitting = false;
        this.showError(err?.error?.message || err?.message || 'Unable To Login: ' + err);
        this.cdr.detectChanges();
      }
    });
  }

  showError(msg: string) {
    this.error = msg;
    this.cdr.detectChanges();

    setTimeout(()=> {
      this.error = null;
      this.cdr.detectChanges();
    }, 4000);
  }
}
