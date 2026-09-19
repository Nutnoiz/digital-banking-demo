import { inject } from '@angular/core';

import {
  HttpErrorResponse,
  HttpInterceptorFn
} from '@angular/common/http';

import {
  catchError,
  throwError
} from 'rxjs';

import {
  Router
} from '@angular/router';

import {
  AuthService
} from '../auth/auth.service';


export const authInterceptor: HttpInterceptorFn = (
  req,
  next
) => {

  const authService =
    inject(AuthService);

  const router =
    inject(Router);


  const token =
    authService.getToken();


  let authenticatedRequest =
    req;


  /*
   * Add JWT Authorization header
   *
   * Only add the header when
   * an access token exists.
   */
  if (token) {

    authenticatedRequest =
      req.clone({
        setHeaders: {
          Authorization:
            `Bearer ${token}`
        }
      });

  }


  return next(
    authenticatedRequest
  ).pipe(

    catchError(
      (error: HttpErrorResponse) => {

        console.error(
          'HTTP ERROR:',
          error.status,
          error.message,
          error.error
        );


        /*
         * ==================================================
         * 401 Unauthorized
         * ==================================================
         *
         * IMPORTANT:
         *
         * Login failure is NOT an expired session.
         *
         * The login endpoint must return
         * its 401 error to LoginComponent.
         */
        if (error.status === 401) {

          const isLoginRequest =
            req.url.endsWith(
              '/api/auth/login'
            );


          if (isLoginRequest) {

            console.warn(
              'Login authentication failed.'
            );

            /*
             * Do NOT logout.
             *
             * Do NOT redirect.
             *
             * Let LoginComponent display
             * the backend authentication error.
             */
            return throwError(
              () => error
            );

          }


          /*
           * 401 from a protected API.
           *
           * The existing session/token
           * is no longer valid.
           */
          console.warn(
            'Authentication expired or unauthorized.'
          );


          authService.logout();


          router.navigate([
            '/login'
          ]);


          return throwError(
            () => error
          );

        }


        /*
         * ==================================================
         * 403 Forbidden
         * ==================================================
         */
        if (error.status === 403) {

          console.warn(
            'Access forbidden.'
          );


          return throwError(
            () => error
          );

        }


        /*
         * ==================================================
         * 400 Bad Request
         * ==================================================
         */
        if (error.status === 400) {

          console.warn(
            'Bad request.',
            error.error
          );


          return throwError(
            () => error
          );

        }


        /*
         * ==================================================
         * 404 Not Found
         * ==================================================
         */
        if (error.status === 404) {

          console.warn(
            'Resource not found.'
          );


          return throwError(
            () => error
          );

        }


        /*
         * ==================================================
         * 500+ Server Error
         * ==================================================
         */
        if (error.status >= 500) {

          console.error(
            'Server error.'
          );


          return throwError(
            () => error
          );

        }


        /*
         * ==================================================
         * Network / Unknown Error
         * ==================================================
         */
        return throwError(
          () => error
        );

      }
    )

  );

};