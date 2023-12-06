import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";

@Component({
  selector: 'app-side-bar',
  templateUrl: './side-bar.component.html',
  styleUrls: ['./side-bar.component.css']
})
export class SideBarComponent implements OnInit {

  constructor(private _router:Router) { }

  ngOnInit(): void {
  }

  goToCreatePost() {
    this._router.navigateByUrl('/create-post');

  }

  goToCreateSubreddit() {
    this._router.navigateByUrl('/create-subreddit');

  }
}
