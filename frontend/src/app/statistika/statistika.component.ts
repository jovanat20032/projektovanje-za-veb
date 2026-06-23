import { Component, OnInit, inject } from '@angular/core';
import { StatistikaService } from '../services/statistika.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-statistika',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './statistika.component.html',
  styleUrl: './statistika.component.css'
})
export class StatistikaComponent implements OnInit {

  private statistikaService = inject(StatistikaService);

  ukupnaPotrosnja: number = 0;

  ngOnInit(): void {
    this.ucitajRezervacijePoSportu();
    this.ucitajMesecnuAktivnost();
    this.ucitajPotrosnju();
  }

  ucitajRezervacijePoSportu() {
    this.statistikaService.getRezervacijePoSportu().subscribe(data => {
      const labels = Object.keys(data);
      const values = Object.values(data);

      new Chart('barChart', {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [{
            label: 'Broj rezervisanih termina',
            data: values,
            backgroundColor: 'rgba(54, 162, 235, 0.6)',
            borderColor: 'rgba(54, 162, 235, 1)',
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          scales: {
            y: { beginAtZero: true, ticks: { stepSize: 1 } }
          }
        }
      });
    });
  }

  ucitajMesecnuAktivnost() {
    this.statistikaService.getMesecnaAktivnost().subscribe(data => {
      const labels = Object.keys(data);
      const values = Object.values(data);

      new Chart('lineChart', {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{
            label: 'Mesečni trend aktivnosti',
            data: values,
            fill: true,
            backgroundColor: 'rgba(75, 192, 192, 0.2)',
            borderColor: 'rgba(75, 192, 192, 1)',
            tension: 0.3
          }]
        },
        options: {
          responsive: true,
          scales: {
            y: { beginAtZero: true, ticks: { stepSize: 1 } }
          }
        }
      });
    });
  }

  ucitajPotrosnju() {
    this.statistikaService.getPotrosnja().subscribe(data => {
      const labels = Object.keys(data);
      const values = Object.values(data) as number[];
      
      this.ukupnaPotrosnja = values.reduce((a, b) => a + b, 0);

      const backgroundColors = [
        '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'
      ];

      new Chart('pieChart', {
        type: 'pie',
        data: {
          labels: labels,
          datasets: [{
            label: 'Potrošnja (RSD)',
            data: values,
            backgroundColor: backgroundColors.slice(0, labels.length)
          }]
        },
        options: {
          responsive: true,
          plugins: {
            legend: { position: 'right' }
          }
        }
      });
    });
  }
}
