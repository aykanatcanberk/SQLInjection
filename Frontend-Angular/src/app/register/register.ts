import { Component, ChangeDetectorRef } from '@angular/core';
import { Api } from '../service/api';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register',
  imports: [CommonModule, FormsModule],
  templateUrl: './register.html',
})
export class Register {

  // Limits
  readonly MAX_NAME_LENGTH = 50;
  readonly MAX_EMAIL_LENGTH = 254;
  readonly MIN_PASSWORD_LENGTH = 8;
  readonly MAX_PASSWORD_LENGTH = 128;
  readonly MAX_PHONE_LENGTH = 20;

  formData: any = {
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    password: ''
  }

  error: any = null;
  submitting: boolean = false;

  // suspicious patterns (frontend ön filtre)
  private suspiciousPatterns: RegExp[] = [
    /('|--|;|\/\*|\*\/|#)/i,
    /\b(union|select|insert|update|delete|drop|alter|create|exec|execute)\b/i,
    /(\bor\b|\band\b).*(=|like)/i,
    /(%00|\\x00|\x00)/i,
    /[\x00-\x1F\x7F]/ 
  ];

  constructor(private apiService: Api, private router: Router, private cdr: ChangeDetectorRef) {}

  private trimInputs() {
    this.formData.firstName = (this.formData.firstName || '').toString().trim();
    this.formData.lastName = (this.formData.lastName || '').toString().trim();
    this.formData.email = (this.formData.email || '').toString().trim();
    this.formData.phoneNumber = (this.formData.phoneNumber || '').toString().trim();
    this.formData.password = (this.formData.password || '').toString().trim();
  }

  private isSuspiciousInput(value: string): boolean {
    if (!value) return false;
    for (const p of this.suspiciousPatterns) {
      if (p.test(value)) return true;
    }
    return false;
  }

  private sanitizeForTransport(value: string): string {
    if (!value) return value;
    let v = value.trim();

    // Try to decode URL-encoded sequences (safe-guard)
    try {
      const decoded = decodeURIComponent(v);
      v = decoded;
    } catch (e) {
      // ignore decode errors
    }

    // Remove control characters (including null byte)
    v = v.replace(/[\x00-\x1F\x7F]+/g, '');

    // Collapse multiple whitespace
    v = v.replace(/\s+/g, ' ');

    // Enforce max length as extra guard
    if (v.length > 2048) v = v.slice(0, 2048);

    return v;
  }

  private basicClientValidation(): string | null {
    if (
      !this.formData.firstName ||
      !this.formData.lastName ||
      !this.formData.email ||
      !this.formData.phoneNumber ||
      !this.formData.password
    ) {
      return 'Please fill all required fields';
    }

    if (this.formData.firstName.length > this.MAX_NAME_LENGTH || this.formData.lastName.length > this.MAX_NAME_LENGTH) {
      return 'Name fields are too long';
    }

    // Email check
    const emailRe = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRe.test(this.formData.email) || this.formData.email.length > this.MAX_EMAIL_LENGTH) {
      return 'Please provide a valid email address';
    }

    // Phone check (basic, allow + and digits)
    const phoneRe = /^\+?[0-9\s\-]{7,20}$/;
    if (!phoneRe.test(this.formData.phoneNumber) || this.formData.phoneNumber.length > this.MAX_PHONE_LENGTH) {
      return 'Please provide a valid phone number';
    }

    if (this.formData.password.length < this.MIN_PASSWORD_LENGTH) {
      return `Password must be at least ${this.MIN_PASSWORD_LENGTH} characters long`;
    }
    if (this.formData.password.length > this.MAX_PASSWORD_LENGTH) {
      return 'Password is too long';
    }

    // Suspicious pattern check
    if (
      this.isSuspiciousInput(this.formData.firstName) ||
      this.isSuspiciousInput(this.formData.lastName) ||
      this.isSuspiciousInput(this.formData.email) ||
      this.isSuspiciousInput(this.formData.phoneNumber) ||
      this.isSuspiciousInput(this.formData.password)
    ) {
      return 'Input contains suspicious characters or patterns';
    }

    return null;
  }

  handleSubmit(){
    // normalize
    this.trimInputs();

    // client validation
    const validationError = this.basicClientValidation();
    if (validationError) {
      this.showError(validationError);
      this.submitting = false;
      this.cdr.detectChanges();
      return;
    }

    // prepare payload (sanitize)
    const payload = {
      firstName: this.sanitizeForTransport(this.formData.firstName),
      lastName: this.sanitizeForTransport(this.formData.lastName),
      email: this.sanitizeForTransport(this.formData.email),
      phoneNumber: this.sanitizeForTransport(this.formData.phoneNumber),
      password: this.sanitizeForTransport(this.formData.password)
    };

    // set submitting flag and update view
    this.submitting = true;
    this.cdr.detectChanges();

    this.apiService.registerUser(payload).subscribe({
      next: (res: any) => {
        this.submitting = false;
        this.cdr.detectChanges();

        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        this.submitting = false;
        this.showError(err?.error?.message || err?.message || 'Unable to Register a user: ' + err);
        this.cdr.detectChanges();
      }
    });
  }

  showError(msg: string){
    this.error = msg;
    this.cdr.detectChanges();

    setTimeout(()=> {
      this.error = null;
      this.cdr.detectChanges();
    }, 4000);
  }

}
