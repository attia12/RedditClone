import { Component, OnInit } from '@angular/core';
import {PostModel} from "../../shared/Post-model";
import {PostService} from "../../shared/post.service";
import {ActivatedRoute, Router} from "@angular/router";
import {throwError} from "rxjs";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {CommentPayload} from "../../comment/comment.payload";
import {CommentService} from "../../comment/comment.service";

@Component({
  selector: 'app-view-post',
  templateUrl: './view-post.component.html',
  styleUrls: ['./view-post.component.css']
})
export class ViewPostComponent implements OnInit {
  postId: any;
  post: any;
  commentForm: FormGroup;
  commentPayload: CommentPayload;
  comments: CommentPayload[]=[];

  constructor(private postService: PostService, private activateRoute: ActivatedRoute,
              private commentService: CommentService, private router: Router) {
    this.postId = this.activateRoute.snapshot.params['id'];

    this.commentForm = new FormGroup({
      text: new FormControl('', Validators.required)
    });
    this.commentPayload = {
      text: '',
      postId: this.postId
    };
  }

  ngOnInit(): void {
    console.log(this.postId)

    this.getPostById();
    this.getCommentsForPost();


  }

  postComment() {
    this.commentPayload.text = this.commentForm.get('text')?.value;
    this.commentService.postComment(this.commentPayload).subscribe(data => {
      this.commentForm.get('text')?.setValue('');
      this.getCommentsForPost();
    }, error => {
      throwError(error);
    })

  }

  private getPostById() {
    this.postService.getPost(this.postId).subscribe(data => {
      //console.log("ddddddddddddddddddddd"+JSON.stringify(data));

      this.post = data;
      console.log(this.post)
    }, error => {
      throwError(error);
    });

  }

  private getCommentsForPost() {
    this.commentService.getAllCommentsForPost(this.postId).subscribe(data => {
      this.comments = data;
    }, error => {
      throwError(error);
    });
  }


}
