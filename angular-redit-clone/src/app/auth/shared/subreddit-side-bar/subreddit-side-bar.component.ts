import { Component, OnInit } from '@angular/core';
import {SubreditService} from "../../../../subredit/subredit.service";
import {SubredditModel} from "../../../../subredit/subbredit-response";

@Component({
  selector: 'app-subreddit-side-bar',
  templateUrl: './subreddit-side-bar.component.html',
  styleUrls: ['./subreddit-side-bar.component.css']
})
export class SubredditSideBarComponent implements OnInit {
  subreddits: Array<SubredditModel> = [];
  displayViewAll!: boolean;

  constructor(private subredditService:SubreditService) {
    this.subredditService.getAllSubreddits().subscribe(data => {
      if (data.length >= 4) {
        this.subreddits = data.splice(0, 3);
        this.displayViewAll = true;
      } else {
        this.subreddits = data;
      }
    });
  }

  ngOnInit(): void {

  }

}
