import { Injectable } from '@angular/core';
import { HttpClient,HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root'
})
export class Api {
  private static BASE_URL = "http://localhost:8080/api";
  private static ENCRYPTION_KEY = "encrypt-key";

  constructor(private http: HttpClient){}

  encryptAndSaveToStorage(key: string , value: string): void{

    const encryptedValue = CryptoJS.AES.encrypt(
      value,
      Api.ENCRYPTION_KEY).toString();

    localStorage.setItem(key,encryptedValue);
  }
  

  private getFromStorageAndDecrypt(key: string): string | null {
    try {
      const encryptedValue = localStorage.getItem(key);
      if (!encryptedValue) return null;
      return CryptoJS.AES.decrypt(
        encryptedValue,
        Api.ENCRYPTION_KEY
      ).toString(CryptoJS.enc.Utf8);
    } catch (error) {
      return null;
    }
  }

  private clearAuth(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
  }

  private getHeader(): HttpHeaders {
    const token = this.getFromStorageAndDecrypt('token');
    return new HttpHeaders({
      Authorization: `Bearer ${token}`,
    });
  }


  //Auth API
  registerUser(body: any): Observable<any> {
    return this.http.post(`${Api.BASE_URL}/auth/register`, body);
  }

  loginUser(body: any): Observable<any> {
    return this.http.post(`${Api.BASE_URL}/auth/login`, body);
  }


   // User API
  myProfile(): Observable<any> {
    return this.http.get(`${Api.BASE_URL}/users/account`, {
      headers: this.getHeader(),
    });
  }

  deleteAccount(): Observable<any> {
    return this.http.delete(`${Api.BASE_URL}/users/delete`, {
      headers: this.getHeader(),
    });
  }

  
  // Auth Check
  logout(): void {
    this.clearAuth();
  }

  isAuthenticated(): boolean {
    const token = this.getFromStorageAndDecrypt('token');
    return !!token;
  }

  isAdmin(): boolean {
    const role = this.getFromStorageAndDecrypt('role');
    return role === 'ADMIN';
  }

  isUser(): boolean {
    const role = this.getFromStorageAndDecrypt('role');
    return role === 'USER';
  }

}
