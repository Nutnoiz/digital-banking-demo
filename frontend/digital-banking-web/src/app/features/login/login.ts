import {
  Component,
  ChangeDetectorRef,
  inject
} from '@angular/core';

import {
  FormsModule
} from '@angular/forms';

import {
  Router
} from '@angular/router';

import {
  AuthService
} from '../../core/auth/auth.service';

import {
  HttpErrorService
} from '../../core/http/http-error.service';

import {
  LoginRequest
} from '../../core/auth/auth.models';


@Component({
  selector: 'app-login',

  standalone: true,

  imports: [
    FormsModule
  ],

  templateUrl: './login.html',

  styleUrl: './login.css'
})
export class Login {

  private readonly authService =
    inject(AuthService);

  private readonly httpErrorService =
    inject(HttpErrorService);

  private readonly router =
    inject(Router);

  private readonly changeDetectorRef =
    inject(ChangeDetectorRef);


  /*
   * ==================================================
   * Demo Account
   * ==================================================
   *
   * Pre-filled credentials for GitHub / portfolio demo.
   */
  username = 'demo';

  password = 'Demo123!';


  loading = false;

  errorMessage = '';


  login(): void {

    console.log(
      'LOGIN STEP 1: submit'
    );


    /*
     * Clear previous error
     */
    this.errorMessage = '';


    /*
     * Validate username
     */
    if (!this.username.trim()) {

      this.errorMessage =
        'Please enter your username.';

      return;

    }


    /*
     * Validate password
     */
    if (!this.password) {

      this.errorMessage =
        'Please enter your password.';

      return;

    }


    /*
     * Build login request
     */
    const request: LoginRequest = {

      username:
        this.username.trim(),

      password:
        this.password

    };


    console.log(
      'LOGIN STEP 2: request username =',
      request.username
    );


    this.loading = true;


    /*
     * Call backend login API
     */
    this.authService
      .login(request)
      .subscribe({

        next: (response) => {

          console.log(
            'LOGIN STEP 3: login success:',
            response
          );


          /*
           * AuthService stores:
           *
           * accessToken
           * username
           */


          this.loading = false;


          console.log(
            'LOGIN STEP 4: navigate to dashboard'
          );


          this.router.navigate([
            '/dashboard'
          ]);


          this.changeDetectorRef.detectChanges();

        },


        error: (error) => {

          console.error(
            'LOGIN API ERROR:',
            error
          );


          /*
           * Use centralized HTTP error handling.
           *
           * Backend 401:
           *
           * Invalid username or password
           */
          this.errorMessage =
            this.httpErrorService.getMessage(
              error,
              'Login failed.'
            );


          this.loading = false;


          console.log(
            'LOGIN STEP 4: error message =',
            this.errorMessage
          );


          this.changeDetectorRef.detectChanges();

        }

      });

  }

}