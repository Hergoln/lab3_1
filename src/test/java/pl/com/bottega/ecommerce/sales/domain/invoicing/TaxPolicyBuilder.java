package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.mockito.Mockito;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class TaxPolicyBuilder {

	private Tax tax;

	private TaxPolicyBuilder() {

	}

	public static TaxPolicyBuilder builder() {
		return new TaxPolicyBuilder();
	}

	public TaxPolicyBuilder withTax(Tax tax) {
		this.tax = tax;
		return this;
	}

	public TaxPolicy build() {
		TaxPolicy taxPolicyMock = Mockito.mock(TaxPolicy.class);
		if (tax == null) {
			tax = new Tax(Money.ZERO, "");
		}
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
		return taxPolicyMock;
	}

}
