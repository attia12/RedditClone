import {EventEmitter, Injectable, Output} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {SingupRequestPayload} from "../signup/singup-request.payload";
import {map, Observable, tap, throwError} from "rxjs";
import {LoginRequestPayload} from "../login/login-request.payload";
import {LoginResponsePayload} from "../login/login-response.payload";
import {LocalStorageService} from "ngx-webstorage";

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  @Output() loggedIn: EventEmitter<boolean> = new EventEmitter();
  @Output() username: EventEmitter<string> = new EventEmitter();
  refreshTokenPayload = {
    refreshToken: this.getRefreshToken(),
    username: this.getUserName()
  }

  constructor(private httpClient:HttpClient,
              private localStorage:LocalStorageService) { }
  signup(signupRequest:SingupRequestPayload):Observable<any>
  {
    return this.httpClient.post('http://localhost:8080/api/auth/signup',signupRequest,{responseType:'text'});
  }
  login(loginRequest:LoginRequestPayload):Observable<boolean>
  {
    return this.httpClient.post<LoginResponsePayload>('http://localhost:8080/api/auth/login',loginRequest).pipe(map(data=>{
      this.localStorage.store('authenticationToken',data.authenticationToken);
      this.localStorage.store('username',data.username);
      this.localStorage.store('refreshToken',data.refreshToken);
      this.localStorage.store('expiresAt',data.expiresAt);
      this.loggedIn.emit(true);
      this.username.emit(data.username)
      return true;

    }));
  }

  getJwtToken() {
    return this.localStorage.retrieve('authenticationToken');
  }

  refreshToken() {
    const refreshTokenPayload = {
      refreshToken: this.getRefreshToken(),
      username: this.getUserName()
    }
    return this.httpClient.post<LoginResponsePayload>('http://localhost:8080/api/auth/refresh/token',
      refreshTokenPayload)
      .pipe(tap(response => {
        this.localStorage.store('authenticationToken', response.authenticationToken);
        this.localStorage.store('expiresAt', response.expiresAt);
      }));

  }

  private getRefreshToken() {
    return this.localStorage.retrieve('refreshToken');

  }

  getUserName() {
    return this.localStorage.retrieve('username');

  }
  isLoggedIn(): boolean {
    return this.getJwtToken() != null;
  }
  logout() {
    this.httpClient.post('http://localhost:8080/api/auth/logout', this.refreshTokenPayload,
      { responseType: 'text' })
      .subscribe(data => {
        console.log(data);
      }, error => {
        throwError(error);
      })
    this.localStorage.clear('authenticationToken');
    this.localStorage.clear('username');
    this.localStorage.clear('refreshToken');
    this.localStorage.clear('expiresAt');
  }
}
