import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { LayoutModule } from '@angular/cdk/layout';
import { HeaderComponent } from './layout/header/header.component';
import { ParticipantComponent } from './layout/participant/participant.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    ParticipantComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    LayoutModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
