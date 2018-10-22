package pl.coderstrust;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
public enum Vat {

  VAT_23(0.23f),
  VAT_8(0.08f),
  VAT_5(0.05f),
  VAT_0(0.00f);

  @NonNull
  @Getter
  private final float value;
}
