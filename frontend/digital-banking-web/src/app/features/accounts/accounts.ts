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
  Router
} from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';

import {
  HttpErrorService
} from '../../core/http/http-error.service';

import {
  Account
} from '../../core/auth/auth.models';

import {
  AccountsService
} from './accounts.service';


@Component({
  selector: 'app-accounts',

  standalone: true,

  imports: [
    CommonModule,
    DecimalPipe
  ],

  templateUrl: './accounts.html',

  styleUrl: './accounts.css'
})
export class Accounts implements OnInit {

  private readonly accountsService =
    inject(AccountsService);

  private readonly authService =
    inject(AuthService);

  private readonly httpErrorService =
    inject(HttpErrorService);

  private readonly router =
    inject(Router);

  private readonly changeDetectorRef =
    inject(ChangeDetectorRef);


  accounts: Account[] = [];

  loading = false;

  errorMessage = '';


  authUsername =
    this.authService.getUsername() ?? 'User';


  ngOnInit(): void {

    console.log(
      'ACCOUNTS STEP 1: ngOnInit()'
    );

    this.loadAccounts();

  }


  loadAccounts(): void {

    console.log(
      'ACCOUNTS STEP 2: loadAccounts()'
    );


    this.loading = true;

    this.errorMessage = '';


    console.log(
      'ACCOUNTS STEP 3: calling AccountsService'
    );


    this.accountsService
      .getAccounts()
      .subscribe({

        next: (accounts) => {

          console.log(
            'ACCOUNTS STEP 4: accounts response:',
            accounts
          );

          console.table(accounts);


          this.accounts = accounts;

          this.loading = false;


          console.log(
            'ACCOUNTS STEP 5: loading =',
            this.loading
          );


          console.log(
            'ACCOUNTS STEP 6: accounts.length =',
            this.accounts.length
          );


          this.changeDetectorRef.detectChanges();

        },


        error: (error) => {

          console.error(
            'ACCOUNTS API ERROR:',
            error
          );


          this.errorMessage =
            this.httpErrorService.getMessage(
              error,
              'Unable to load accounts.'
            );


          this.loading = false;


          this.changeDetectorRef.detectChanges();

        }

      });

  }


  viewTransactions(
    accountId: number
  ): void {

    console.log(
      'ACCOUNTS: view transactions:',
      accountId
    );


    this.router.navigate([
      '/accounts',
      accountId,
      'transactions'
    ]);

  }


  goToTransfer(): void {

    console.log(
      'ACCOUNTS: go to transfer'
    );


    this.router.navigate([
      '/transfer'
    ]);

  }

}