import {Component, Input, OnInit} from '@angular/core';
import {PostService} from "../../../shared/post.service";



import {faComments} from "@fortawesome/free-solid-svg-icons";
import {PostModel} from "../../../shared/Post-model";
import {Router} from "@angular/router";


@Component({
  selector: 'app-post-title',
  templateUrl: './post-title.component.html',
  styleUrls: ['./post-title.component.css']
})
export class PostTitleComponent implements OnInit {
  // posts$: Array<PostModel>=[];

  @Input()
  posts: PostModel[]=[];

  faComments=faComments;

  constructor(private postService:PostService,private _router:Router) {
    // this.postService.getAllPosts().subscribe(post=>{
    //   console.log(post);
    //   this.posts$=post;
    //
    // },error => {
    //   console.log(error);
    // })
  }

  ngOnInit(): void {
  }

  goToPost(id: number) {
    this._router.navigateByUrl('/view-post/' + id);

  }
}
