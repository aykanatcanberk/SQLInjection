import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Api } from '../service/api'; 
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';


@Component({
  selector: 'app-profile',
  standalone: true,         
  imports: [CommonModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.css'] 
})
export class Profile {

  user: any = null;
  bookings: any[] = [];
  error: any = null;

constructor(private apiService: Api, private router: Router, private cd: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.fetchUserProfile();
  }

fetchUserProfile() {
  this.apiService.myProfile().subscribe({
    next: (response: any) => {
      this.user = response.user;
      this.cd.detectChanges();
    },
    error: (err) => {
      this.showError(err?.error?.message || 'Error getting my profile info');
      this.cd.detectChanges();
    },
  });
}

  showError(msg: string) {
    this.error = msg;
    setTimeout(() => {
      this.error = null;
    }, 4000);
  }

  handleLogout() {
    this.apiService.logout();
    this.router.navigate(['/home']);
  }

  handleEditProfile() {
    this.router.navigate(['/edit-profile']);
  }

}
