import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {LoginRequestPayload} from "./login-request.payload";
import {AuthService} from "../shared/auth.service";
import {ToastrService} from "ngx-toastr";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  isError: any;
  loginForm!: FormGroup;
  loginRequestPayload:LoginRequestPayload;
   registerSuccessMessage!: string;

  constructor(private authService:AuthService,
              private toast:ToastrService,
              private router:Router,
              private _route:ActivatedRoute) {
    this.loginRequestPayload={
      username:'',
      password:''
    };
  }

  ngOnInit(): void {
    this.loginForm=new FormGroup({
      username:new FormControl('',Validators.required),
      password:new FormControl('',Validators.required)
    });
    this._route.queryParams.subscribe(params=>{
      if(params['registered'] !== undefined && params['registered'] ==='true')
      {
        this.toast.success('Signup Successufully');
        this.registerSuccessMessage = 'Please Check your inbox for activation email '
          + 'activate your account before you Login!';
      }


    });
  }

  login() {
    this.loginRequestPayload.username=this.loginForm.get('username')?.value;
    this.loginRequestPayload.password=this.loginForm.get('password')?.value;
    this.authService.login(this.loginRequestPayload).subscribe((data)=>{

      if (data)
      {
        this.isError=false;
        this.router.navigateByUrl('/');
        this.toast.success('Login Successfully')
      }
      else {
        this.isError=true;
      }

    },error => {
      console.log(error);
    })

  }
}
