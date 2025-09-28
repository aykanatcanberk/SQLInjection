import { Component, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Api } from '../service/api';

@Component({
  selector: 'app-editprofile',
  imports: [CommonModule],
  templateUrl: './editprofile.html',
  styleUrls: ['./editprofile.css']
})
export class Editprofile {
  user: any = null;
  error: any = null;

  constructor(private apiService: Api, private router: Router, private cd: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.fetchUserProfile();
  }

  fetchUserProfile(): void {
    this.apiService.myProfile().subscribe({
      next: (response: any) => {
        this.user = response.user;
        this.cd.detectChanges();
      },
      error: (err) => {
        this.showError(err?.error?.message || 'Error fetching user profile');
        this.cd.detectChanges();
      },
    });
  }

  showError(message: string) {
    this.error = message;
    setTimeout(() => {
      this.error = null;
      this.cd.detectChanges();
    }, 4000);
  }

  handleDeleteProfile(): void {
    if (!window.confirm(
      'Are you sure you want to delete your account? If you delete your account, you will lose access to your profile and booking history.'
    )) return;

    this.apiService.deleteAccount().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      },
      error: (err) => {
        this.showError(err?.error?.message || 'Error Deleting account');
      },
    });
  }
}
