import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  
  uloga: string | null = null;
  korisnickoIme: string | null = null;
  private router = inject(Router);

  ngOnInit(): void {
    // We check localStorage. To make it dynamic, we could use an auth service, 
    // but for simple cases, we can check it on route changes or just read it here.
    // A better approach is to subscribe to router events to update this.
    this.router.events.subscribe(() => {
      this.uloga = localStorage.getItem('uloga');
      this.korisnickoIme = localStorage.getItem('korisnickoIme');
    });
  }

  logout() {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('uloga');
    localStorage.removeItem('korisnickoIme');
    this.router.navigate(['/prijava']);
  }
}
