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

import { forkJoin } from 'rxjs';

import { AuthService } from '../../core/auth/auth.service';

import {
  Account,
  BankTransaction
} from '../../core/auth/auth.models';

import {
  HttpErrorService
} from '../../core/http/http-error.service';

import {
  AccountsService
} from '../accounts/accounts.service';

import {
  TransactionsService
} from '../transactions/transactions.service';


@Component({
  selector: 'app-dashboard',

  standalone: true,

  imports: [
    CommonModule,
    DecimalPipe
  ],

  templateUrl: './dashboard.html',

  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  private readonly authService =
    inject(AuthService);

  private readonly accountsService =
    inject(AccountsService);

  private readonly transactionsService =
    inject(TransactionsService);

  private readonly httpErrorService =
    inject(HttpErrorService);

  private readonly router =
    inject(Router);

  private readonly changeDetectorRef =
    inject(ChangeDetectorRef);


  username =
    this.authService.getUsername() ?? 'User';


  accounts: Account[] = [];

  recentTransactions: BankTransaction[] = [];


  totalBalance = 0;

  loading = true;

  errorMessage = '';


  ngOnInit(): void {

    console.log(
      'DASHBOARD STEP 1: ngOnInit()'
    );

    this.loadDashboard();

  }


  loadDashboard(): void {

    console.log(
      'DASHBOARD STEP 2: loading accounts'
    );


    this.loading = true;

    this.errorMessage = '';


    this.accountsService
      .getAccounts()
      .subscribe({

        next: (accounts) => {

          console.log(
            'DASHBOARD STEP 3: accounts:',
            accounts
          );

          console.table(accounts);


          this.accounts = accounts;


          this.totalBalance =
            accounts.reduce(
              (total, account) =>
                total + Number(account.balance),
              0
            );


          /*
           * No accounts
           */
          if (accounts.length === 0) {

            this.recentTransactions = [];

            this.loading = false;

            this.changeDetectorRef.detectChanges();

            return;

          }


          console.log(
            'DASHBOARD STEP 4: loading transactions'
          );


          /*
           * Load transactions
           * for every account.
           */
          const requests =
            accounts.map(account =>
              this.transactionsService
                .getTransactions(account.id)
            );


          forkJoin(requests)
            .subscribe({

              next: (transactionLists) => {

                console.log(
                  'DASHBOARD STEP 5: transaction lists:',
                  transactionLists
                );


                const allTransactions =
                  transactionLists.flat();


                console.log(
                  'DASHBOARD STEP 6: all transactions:',
                  allTransactions
                );


                /*
                 * Sort newest first
                 * and keep only 5.
                 */
                this.recentTransactions =
                  allTransactions
                    .sort(
                      (a, b) =>
                        new Date(b.createdAt).getTime() -
                        new Date(a.createdAt).getTime()
                    )
                    .slice(0, 5);


                this.loading = false;


                console.log(
                  'DASHBOARD STEP 7: recent transactions:',
                  this.recentTransactions
                );


                this.changeDetectorRef.detectChanges();

              },


              error: (error) => {

                console.error(
                  'DASHBOARD TRANSACTION ERROR:',
                  error
                );


                this.errorMessage =
                  this.httpErrorService.getMessage(
                    error,
                    'Unable to load dashboard transactions.'
                  );


                this.loading = false;


                this.changeDetectorRef.detectChanges();

              }

            });

        },


        error: (error) => {

          console.error(
            'DASHBOARD ACCOUNT ERROR:',
            error
          );


          this.errorMessage =
            this.httpErrorService.getMessage(
              error,
              'Unable to load dashboard.'
            );


          this.loading = false;


          this.changeDetectorRef.detectChanges();

        }

      });

  }


  getAccountNumber(
    accountId: number
  ): string {

    const account =
      this.accounts.find(
        item => item.id === accountId
      );


    return account?.accountNo ?? '-';

  }


  getTransactionAmountClass(
    transaction: BankTransaction
  ): string {

    if (
      transaction.transactionType ===
      'TRANSFER_OUT'
    ) {

      return 'amount-out';

    }


    if (
      transaction.transactionType ===
      'TRANSFER_IN'
    ) {

      return 'amount-in';

    }


    if (
      transaction.transactionType ===
      'DEPOSIT'
    ) {

      return 'amount-in';

    }


    return '';

  }


  getTransactionPrefix(
    transaction: BankTransaction
  ): string {

    if (
      transaction.transactionType ===
      'TRANSFER_OUT'
    ) {

      return '-';

    }


    if (
      transaction.transactionType ===
      'TRANSFER_IN'
    ) {

      return '+';

    }


    if (
      transaction.transactionType ===
      'DEPOSIT'
    ) {

      return '+';

    }


    return '';

  }


  viewTransactions(
    accountId: number
  ): void {

    console.log(
      'DASHBOARD: view transactions:',
      accountId
    );


    this.router.navigate([
      '/accounts',
      accountId,
      'transactions'
    ]);

  }


  goToAccounts(): void {

    console.log(
      'DASHBOARD: go to accounts'
    );


    this.router.navigate([
      '/accounts'
    ]);

  }


  goToTransfer(): void {

    console.log(
      'DASHBOARD: go to transfer'
    );


    this.router.navigate([
      '/transfer'
    ]);

  }

}