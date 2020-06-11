import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImdbTestModule } from '../../../test.module';
import { SeriesDetailComponent } from 'app/entities/series/series-detail.component';
import { Series } from 'app/shared/model/series.model';

describe('Component Tests', () => {
  describe('Series Management Detail Component', () => {
    let comp: SeriesDetailComponent;
    let fixture: ComponentFixture<SeriesDetailComponent>;
    const route = ({ data: of({ series: new Series(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ImdbTestModule],
        declarations: [SeriesDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(SeriesDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SeriesDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load series on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.series).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
