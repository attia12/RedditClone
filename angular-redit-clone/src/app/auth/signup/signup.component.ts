import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {SingupRequestPayload} from "./singup-request.payload";
import {AuthService} from "../shared/auth.service";
import {ToastrService} from "ngx-toastr";
import {Router} from "@angular/router";

@Component({
  selector: 'app-signup',
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.css']
})
export class SignupComponent implements OnInit {
  signupRequestPayload:SingupRequestPayload;
  signupForm!:FormGroup;

  constructor(private authService:AuthService,
              private toast:ToastrService,
              private router:Router) {
    this.signupRequestPayload={
      username:'',
      email:'',
      password:''
    };
  }

  ngOnInit(): void {
    this.signupForm=new FormGroup({
      username:new FormControl('',Validators.required),
      email:new FormControl('',[Validators.required,Validators.email]),
      password:new FormControl('',Validators.required),
    });
  }

  signup() {
    this.signupRequestPayload.email=this.signupForm.get('email')?.value;
    this.signupRequestPayload.username=this.signupForm.get('username')?.value;
    this.signupRequestPayload.password=this.signupForm.get('password')?.value;
    this.authService.signup(this.signupRequestPayload).subscribe((data)=>{
      console.log(data);
      this.router.navigate(['login'],{queryParams:{registered:'true'}})

    },error => {
      console.log(error);
      this.toast.error('Registration Failed! Please try again ');

    });

  }
}
