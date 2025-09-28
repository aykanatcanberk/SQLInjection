import { Component } from '@angular/core';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {

  searchResults: any[] = [] 

  handleSearchResult(results: any[]){
    this.searchResults = results
  }
}
