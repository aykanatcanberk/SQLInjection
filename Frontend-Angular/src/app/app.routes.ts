import { Routes } from '@angular/router';
import { Login } from './login/login';
import { Register } from './register/register';
import { Profile } from './profile/profile';
import { Guard } from './service/guard';
import { Editprofile } from './editprofile/editprofile';
import { Home } from './home/home';

export const routes: Routes = [
    {path: 'login' , component: Login},
    {path: 'register', component: Register},

    {path: 'profile', component: Profile , canActivate: [Guard]},
    {path: 'edit-profile', component: Editprofile , canActivate: [Guard]},

    {path:'home', component: Home},

    {path: '**' , redirectTo: 'login'}

];