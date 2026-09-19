import { Injectable } from '@angular/core';

import {
  HttpErrorResponse
} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class HttpErrorService {

  getMessage(
    error: HttpErrorResponse,
    fallbackMessage = 'An unexpected error occurred.'
  ): string {

    /*
     * Backend returned a structured message.
     */
    const backendMessage = this.getBackendMessage(error);

    if (backendMessage) {
      return backendMessage;
    }


    /*
     * HTTP status based messages.
     */
    switch (error.status) {

      case 0:
        return 'Unable to connect to the server. Please check your connection.';

      case 400:
        return 'Invalid request. Please check your information.';

      case 401:
        return 'Your session has expired. Please login again.';

      case 403:
        return 'You do not have permission to perform this action.';

      case 404:
        return 'The requested resource was not found.';

      case 409:
        return 'The request could not be completed because of a conflict.';

      case 422:
        return 'The submitted information is invalid.';

      case 500:
        return 'The server encountered an error. Please try again later.';

      case 502:
      case 503:
      case 504:
        return 'The server is temporarily unavailable. Please try again later.';

      default:
        return fallbackMessage;
    }
  }


  private getBackendMessage(
    error: HttpErrorResponse
  ): string | null {

    const body = error.error;

    if (!body) {
      return null;
    }


    /*
     * Example:
     *
     * {
     *   "message": "Insufficient balance"
     * }
     */
    if (
      typeof body === 'object' &&
      typeof body.message === 'string' &&
      body.message.trim()
    ) {
      return body.message.trim();
    }


    /*
     * Backend may return plain text.
     */
    if (
      typeof body === 'string' &&
      body.trim()
    ) {
      return body.trim();
    }


    return null;
  }
}