import {
  Component,
  inject
} from '@angular/core';

import {
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet
} from '@angular/router';

import { AuthService } from '../core/auth/auth.service';


@Component({
  selector: 'app-layout',

  standalone: true,

  imports: [
    RouterLink,
    RouterLinkActive,
    RouterOutlet
  ],

  templateUrl: './layout.html',

  styleUrl: './layout.css'
})
export class Layout {

  private readonly authService =
    inject(AuthService);

  private readonly router =
    inject(Router);


  username =
    this.authService.getUsername() ?? 'User';


  logout(): void {

    console.log(
      'LAYOUT: logout'
    );

    this.authService.logout();

    this.router.navigate([
      '/login'
    ]);
  }

}