import { Component, OnInit } from '@angular/core';
import {SubredditModel} from "../../../subredit/subbredit-response";
import {SubreditService} from "../../../subredit/subredit.service";
import {throwError} from "rxjs";

@Component({
  selector: 'app-list-subreddits',
  templateUrl: './list-subreddits.component.html',
  styleUrls: ['./list-subreddits.component.css']
})
export class ListSubredditsComponent implements OnInit {
  subreddits!: Array<SubredditModel>;

  constructor(private subredditService: SubreditService) { }

  ngOnInit(): void {
    this.subredditService.getAllSubreddits().subscribe(data => {
      this.subreddits = data;
    }, error => {
      throwError(error);
    })
  }

}
