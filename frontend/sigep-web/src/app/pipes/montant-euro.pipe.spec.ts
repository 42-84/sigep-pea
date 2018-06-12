import { MontantEuroPipe } from './montant-euro.pipe';

describe('MontantEuroPipe', () => {
  const pipe = new MontantEuroPipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('formate un montant avec milliers et decimales', () => {
    expect(pipe.transform(1234.5)).toBe('1 234,50 €');
  });

  it('formate un montant negatif', () => {
    expect(pipe.transform(-3612.4)).toBe('-3 612,40 €');
  });

  it('renvoie une chaine vide pour null', () => {
    expect(pipe.transform(null)).toBe('');
  });
});
