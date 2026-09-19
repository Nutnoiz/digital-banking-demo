import {
  Component,
  OnInit,
  ChangeDetectorRef,
  inject
} from '@angular/core';

import {
  CommonModule,
  DecimalPipe
} from '@angular/common';

import {
  FormsModule
} from '@angular/forms';

import {
  Router,
  RouterLink
} from '@angular/router';

import { AccountsService } from '../accounts/accounts.service';

import { TransferService } from './transfer.service';

import {
  Account,
  TransferRequest,
  TransferResponse
} from '../../core/auth/auth.models';

import {
  HttpErrorService
} from '../../core/http/http-error.service';


@Component({
  selector: 'app-transfer',

  standalone: true,

  imports: [
    CommonModule,
    DecimalPipe,
    FormsModule,
    RouterLink
  ],

  templateUrl: './transfer.html',

  styleUrl: './transfer.css'
})
export class Transfer implements OnInit {

  private readonly accountsService =
    inject(AccountsService);

  private readonly transferService =
    inject(TransferService);

  private readonly httpErrorService =
    inject(HttpErrorService);

  private readonly router =
    inject(Router);

  private readonly changeDetectorRef =
    inject(ChangeDetectorRef);


  accounts: Account[] = [];

  sourceAccountId: number | null = null;

  destinationAccountId: number | null = null;

  amount: number | null = null;

  description = '';


  loading = false;

  loadingAccounts = true;

  errorMessage = '';

  successMessage = '';


  transferResult:
    TransferResponse | null = null;


  ngOnInit(): void {

    console.log(
      'TRANSFER STEP 1: ngOnInit()'
    );

    this.loadAccounts();

  }


  loadAccounts(): void {

    console.log(
      'TRANSFER STEP 2: loading accounts'
    );


    this.loadingAccounts = true;

    this.errorMessage = '';


    this.accountsService
      .getAccounts()
      .subscribe({

        next: (accounts) => {

          console.log(
            'TRANSFER STEP 3: accounts response:',
            accounts
          );

          console.table(accounts);


          this.accounts = accounts;

          this.loadingAccounts = false;


          this.changeDetectorRef.detectChanges();

        },


        error: (error) => {

          console.error(
            'TRANSFER ACCOUNT ERROR:',
            error
          );


          this.errorMessage =
            this.httpErrorService.getMessage(
              error,
              'Unable to load accounts.'
            );


          this.loadingAccounts = false;


          this.changeDetectorRef.detectChanges();

        }

      });

  }


  transfer(): void {

    console.log(
      'TRANSFER STEP 4: submit'
    );


    this.errorMessage = '';

    this.successMessage = '';

    this.transferResult = null;


    /*
     * Validate source account
     */
    if (
      this.sourceAccountId === null
    ) {

      this.errorMessage =
        'Please select a source account.';

      return;

    }


    /*
     * Validate destination account
     */
    if (
      this.destinationAccountId === null
    ) {

      this.errorMessage =
        'Please select a destination account.';

      return;

    }


    /*
     * Source and destination
     * must be different.
     */
    if (
      this.sourceAccountId ===
      this.destinationAccountId
    ) {

      this.errorMessage =
        'Source and destination accounts must be different.';

      return;

    }


    /*
     * Validate amount
     */
    if (
      this.amount === null ||
      !Number.isFinite(this.amount) ||
      this.amount <= 0
    ) {

      this.errorMessage =
        'Amount must be greater than zero.';

      return;

    }


    /*
     * Validate decimal places
     */
    const amountText =
      this.amount.toString();

    const decimalPart =
      amountText.split('.')[1] ?? '';


    if (
      decimalPart.length > 2
    ) {

      this.errorMessage =
        'Amount must have at most 2 decimal places.';

      return;

    }


    /*
     * Validate maximum amount
     */
    if (
      this.amount >
      9999999999999999.99
    ) {

      this.errorMessage =
        'Amount is too large.';

      return;

    }


    /*
     * Validate description
     */
    const trimmedDescription =
      this.description.trim();


    if (
      trimmedDescription.length > 255
    ) {

      this.errorMessage =
        'Description must not exceed 255 characters.';

      return;

    }


    /*
     * Build API request
     */
    const request: TransferRequest = {

      sourceAccountId:
        this.sourceAccountId,

      destinationAccountId:
        this.destinationAccountId,

      amount:
        this.amount,

      description:
        trimmedDescription || undefined

    };


    console.log(
      'TRANSFER STEP 5: request:',
      request
    );


    this.loading = true;


    this.transferService
      .transfer(request)
      .subscribe({

        next: (response) => {

          console.log(
            'TRANSFER STEP 6: response:',
            response
          );


          this.transferResult =
            response;


          this.successMessage =
            'Transfer completed successfully.';


          this.loading = false;


          this.changeDetectorRef.detectChanges();

        },


        error: (error) => {

          console.error(
            'TRANSFER STEP 6: API ERROR:',
            error
          );


          this.errorMessage =
            this.httpErrorService.getMessage(
              error,
              'Transfer failed.'
            );


          this.loading = false;


          this.changeDetectorRef.detectChanges();

        }

      });

  }


  resetTransfer(): void {

    console.log(
      'TRANSFER: reset form'
    );


    this.sourceAccountId = null;

    this.destinationAccountId = null;

    this.amount = null;

    this.description = '';

    this.errorMessage = '';

    this.successMessage = '';

    this.transferResult = null;

    this.loading = false;


    this.changeDetectorRef.detectChanges();

  }


  viewSourceTransactions(): void {

    if (!this.transferResult) {
      return;
    }


    this.router.navigate([
      '/accounts',
      this.transferResult.sourceAccountId,
      'transactions'
    ]);

  }


  backToAccounts(): void {

    this.router.navigate([
      '/accounts'
    ]);

  }


  goToDashboard(): void {

    this.router.navigate([
      '/dashboard'
    ]);

  }

}