package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookKeeperTest {
	@Test
	public void zadanieWydaniaFakturyZJednaPozycjaPowinnoZwrocicFaktureZJednaPozycja() {
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = Mockito.mock(TaxPolicy.class);
		Tax tax = new Tax(Money.ZERO, "");
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);

		InvoiceRequest req = new InvoiceRequest(Mockito.mock(ClientData.class));
		Product prod = new Product(
				Mockito.mock(Id.class),
				Mockito.mock(Money.class),
				"",
				ProductType.STANDARD
		);
		RequestItem item = new RequestItem(
				prod.generateSnapshot(),
				1,
				Money.ZERO
		);
		req.add(item);
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals(1, invoice.getItems().size());
	}

	@Test
	public void zadanieWydaniaFakturyZDwiemaPozycjamiPowinnoWywolacMetodeCalculateTaxDwaRazy() {
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = Mockito.mock(TaxPolicy.class);
		Tax tax = new Tax(Money.ZERO, "");
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);

		InvoiceRequest req = new InvoiceRequest(Mockito.mock(ClientData.class));
		Product prod = new Product(
				Mockito.mock(Id.class),
				Mockito.mock(Money.class),
				"",
				ProductType.STANDARD
		);

		RequestItem item = new RequestItem(
				prod.generateSnapshot(),
				1,
				Money.ZERO
		);
		req.add(item);
		req.add(item);

		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);
		assertEquals(2, invoice.getItems().size());
		Mockito.verify(taxPolicyMock, Mockito.times(2)).calculateTax(Mockito.any(), Mockito.any());
	}

	// kolejne testy
	// // 1.
}