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

	// testy stanu
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
	public void zadanieWydaniaFakturyBezPozycjiPowinnoZwrocicFuktureBezPozycji() {
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = Mockito.mock(TaxPolicy.class);
		Tax tax = new Tax(Money.ZERO, "");
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);

		InvoiceRequest req = new InvoiceRequest(Mockito.mock(ClientData.class));
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals(0, invoice.getItems().size());
	}

	@Test
	public void fakturaWykonanaDlaDanegoKlientaFaktyczniePosiadaDaneTegoKlienta() {
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = Mockito.mock(TaxPolicy.class);
		ClientData client = new ClientData(new Id("007"), "James Bond");
		InvoiceRequest req = new InvoiceRequest(client);
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals("007", invoice.getClient().getAggregateId().toString());
		assertEquals("James Bond", invoice.getClient().getName());
	}

	// testy zachowania
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

	@Test
	public void zadanieWydaniaFakturyBezPozycjiPowinnoWywolacMetodeCalculateTaxZeroRazy() {
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = Mockito.mock(TaxPolicy.class);
		Tax tax = new Tax(Money.ZERO, "");
		Mockito.when(taxPolicyMock.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);

		InvoiceRequest req = new InvoiceRequest(Mockito.mock(ClientData.class));
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals(0, invoice.getItems().size());
		Mockito.verify(taxPolicyMock, Mockito.times(0)).calculateTax(Mockito.any(), Mockito.any());
	}

	@Test
	public void wykonanieFakturyWywolujeMetodeCreateKlasyInvoiceFactoryDokladnieRaz() {
		InvoiceFactory invoiceFactoryMock = Mockito.mock(InvoiceFactory.class);
		BookKeeper bookKeeper = new BookKeeper(invoiceFactoryMock);

		TaxPolicy taxPolicyMock = Mockito.mock(TaxPolicy.class);
		InvoiceRequest req = Mockito.mock(InvoiceRequest.class);
		bookKeeper.issuance(req, taxPolicyMock);

		Mockito.verify(invoiceFactoryMock, Mockito.times(1)).create(Mockito.any());
	}
}