import { Component, inject, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ObjekatService } from '../services/objekat.service';
import { RezervacijaService } from '../services/rezervacija.service';
import { TrenerService } from '../services/trener.service';

import { FullCalendarModule } from '@fullcalendar/angular';
import { CalendarOptions, EventDropArg } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';

@Component({
  selector: 'app-zaposleni-kalendar',
  standalone: true,
  imports: [CommonModule, FormsModule, FullCalendarModule],
  templateUrl: './zaposleni-kalendar.component.html',
  styleUrls: ['./zaposleni-kalendar.component.css']
})
export class ZaposleniKalendarComponent implements OnInit {
  private objekatService = inject(ObjekatService);
  private rezervacijaService = inject(RezervacijaService);
  private trenerService = inject(TrenerService);

  objekti: any[] = [];
  selektovaniObjekatId: number | null = null;

  tereni: any[] = [];
  selektovaniTerenId: number | null = null;
  selektovaniTerenObj: any = null;

  korisnickoIme: string = '';

  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
    initialView: 'timeGridWeek',
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay'
    },
    editable: false, // by default false
    events: [],
    eventDrop: this.handleEventDrop.bind(this)
  };

  ngOnInit() {
    this.korisnickoIme = localStorage.getItem('korisnickoIme') || '';
    if (this.korisnickoIme) {
      this.objekatService.getObjektiZaZaposlenog(this.korisnickoIme).subscribe(res => {
        this.objekti = res;
      });
    }
  }

  onObjekatChange() {
    this.selektovaniTerenId = null;
    this.selektovaniTerenObj = null;
    this.tereni = [];
    this.calendarOptions.events = [];

    if (this.selektovaniObjekatId) {
      this.objekatService.getObjekatDetalji(this.selektovaniObjekatId).subscribe(res => {
        this.tereni = res.tereni;
      });
    }
  }

  onTerenChange() {
    if (!this.selektovaniTerenId) {
      this.calendarOptions.events = [];
      return;
    }

    this.selektovaniTerenObj = this.tereni.find(t => t.id == this.selektovaniTerenId);
    
    // Provera za zatvorene hale
    // Pretpostavka je da tip terena u bazi ima ključnu reč 'zatvoren'
    const tip = this.selektovaniTerenObj?.tip?.toLowerCase() || '';
    const isZatvoreno = tip.includes('zatvoren');
    this.calendarOptions.editable = isZatvoreno;

    this.ucitajDogadjaje();
  }

  ucitajDogadjaje() {
    if (!this.selektovaniTerenId) return;

    let events: any[] = [];

    this.rezervacijaService.getRezervacijeZaTeren(this.selektovaniTerenId).subscribe(rezervacije => {
      rezervacije.forEach((r: any) => {
        events.push({
          id: 'rez_' + r.id,
          title: 'Rezervacija', // Uklonjen sport i korisnik da bi vise licilo na sliku
          start: r.vremeOd,
          end: r.vremeDo,
          backgroundColor: '#95d5b2', // Svetlija zelena, kao na slici
          borderColor: '#74c69d',
          textColor: '#1b4332',
          extendedProps: { tip: 'rezervacija', dbId: r.id }
        });
      });

      this.trenerService.getTreninziZaTeren(this.selektovaniTerenId!).subscribe(treninzi => {
        treninzi.forEach((t: any) => {
          const start = new Date(t.datumVreme);
          const end = new Date(start.getTime() + 60 * 60000); // 1h

          events.push({
            id: 'tr_' + t.id,
            title: 'Trening', // Samo "Trening" kao na slici
            start: start.toISOString(),
            end: end.toISOString(),
            backgroundColor: '#4ea8de', // Plava kao na slici
            borderColor: '#0077b6',
            textColor: '#ffffff',
            extendedProps: { tip: 'trening', dbId: t.id }
          });
        });

        this.calendarOptions.events = events;
      });
    });
  }

  handleEventDrop(dropInfo: EventDropArg) {
    const event = dropInfo.event;
    const dbId = event.extendedProps['dbId'];
    const tip = event.extendedProps['tip'];

    const formatLocalTime = (date: Date) => {
      const pad = (n: number) => n < 10 ? '0' + n : n;
      return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate()) + 'T' +
             pad(date.getHours()) + ':' + pad(date.getMinutes()) + ':' + pad(date.getSeconds());
    };

    const novoVremeOd = formatLocalTime(event.start!);
    const novoVremeDo = event.end ? formatLocalTime(event.end) : formatLocalTime(new Date(event.start!.getTime() + 60*60000));

    if (tip === 'rezervacija') {
      this.rezervacijaService.pomeriRezervaciju(dbId, novoVremeOd!, novoVremeDo!).subscribe({
        next: () => {
          alert('Rezervacija uspešno pomerena.');
        },
        error: (err) => {
          alert('Greška pri pomeranju (termin je možda zauzet).');
          dropInfo.revert();
        }
      });
    } else if (tip === 'trening') {
      this.trenerService.pomeriTrening(dbId, novoVremeOd!).subscribe({
        next: () => {
          alert('Trening uspešno pomeren.');
        },
        error: (err) => {
          alert('Greška pri pomeranju treninga.');
          dropInfo.revert();
        }
      });
    }
  }
}
