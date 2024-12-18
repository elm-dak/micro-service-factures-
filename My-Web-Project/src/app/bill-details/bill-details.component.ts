import { Component } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-bill-details',
  standalone: false,

  templateUrl: './bill-details.component.html',
  styleUrl: './bill-details.component.css'
})
export class BillDetailsComponent {
  billDetails: any;
  billId!:number;
  constructor(private http: HttpClient,private router:Router,private route:ActivatedRoute) {
    this.billId = this.route.snapshot.params['billId'];
  }

  ngOnInit() {
    this.http.get('http://localhost:8888/BILLING-SERVICE/fullBills/'+this.billId).subscribe({
      next: (data) => {
        this.billDetails = data;
      },
      error: (err) => {
        console.error('Erreur lors de la récupération des produits:', err);
      }
    });
  }


  getBillsDetails(b: any) {
      this.router.navigate(['/bill-details', b.id]);
    }





}
