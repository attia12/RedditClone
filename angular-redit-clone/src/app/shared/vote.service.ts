import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {VotePayload} from "../auth/shared/vote-button/vote-payload";

@Injectable({
  providedIn: 'root'
})
export class VoteService {

  constructor(private http: HttpClient) { }
  vote(votePayload: VotePayload): Observable<any> {
    return this.http.post('http://localhost:8080/api/votes/', votePayload);
  }
}
