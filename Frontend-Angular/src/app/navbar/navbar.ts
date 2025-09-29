import { Component } from '@angular/core';
import { Api } from '../service/api';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule , RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {

   constructor(private router: Router, private apiService: Api){}

  get isAuthemticated():boolean{
    return this.apiService.isAuthenticated();
  }

  get isUser():boolean{
    return this.apiService.isUser();
  }

  get isAdmin():boolean{
    return this.apiService.isAdmin();
  }

  handleLogout(): void{
    const isLogout = window.confirm("Are you sure you want to logout? ")
    if (isLogout) {
      this.apiService.logout();
      this.router.navigate(['/home'])
    }
  }

}
