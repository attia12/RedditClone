import {Component, Input, OnInit} from '@angular/core';
import {PostModel} from "../../../shared/Post-model";
import {faArrowUp,faArrowDown} from "@fortawesome/free-solid-svg-icons";
import {VoteService} from "../../../shared/vote.service";
import {AuthService} from "../auth.service";
import {PostService} from "../../../shared/post.service";
import {ToastrService} from "ngx-toastr";
import {VotePayload} from "./vote-payload";
import {VoteType} from "./vote-type";
import {throwError} from "rxjs";

@Component({
  selector: 'app-vote-button',
  templateUrl: './vote-button.component.html',
  styleUrls: ['./vote-button.component.css']
})
export class VoteButtonComponent implements OnInit {
  @Input() post!: PostModel ;
  faArrowUp = faArrowUp;
  faArrowDown = faArrowDown;
  downvoteColor: any;
  upvoteColor: any;

  votePayload: VotePayload;

  constructor(private voteService: VoteService,
              private authService: AuthService,
              private postService: PostService, private toastr: ToastrService) {
    this.votePayload = {
      voteType: undefined,
      postId: undefined
    }
  }

  ngOnInit(): void {
    this.updateVoteDetails();
  }

  upvotePost() {
    this.votePayload.voteType = VoteType.UPVOTE;
    this.vote();
    this.downvoteColor = '';

  }

  downvotePost() {
    this.votePayload.voteType = VoteType.DOWNVOTE;
    this.vote();
    this.upvoteColor = '';

  }
  private vote() {
    this.votePayload.postId = this.post.id;
    this.voteService.vote(this.votePayload).subscribe(() => {
      this.updateVoteDetails();
    }, error => {
      this.toastr.error(error.error.message);
      throwError(error);
    });
  }
  private updateVoteDetails() {
    this.postService.getPost(this.post.id).subscribe(post => {
      this.post = post;
    });
  }


}
