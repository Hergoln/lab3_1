package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookKeeperTest {

	static private BookKeeper bookKeeper;
	static private TaxPolicy taxPolicyMock;

	@BeforeAll
	static public void init() {
		bookKeeper = new BookKeeper(new InvoiceFactory());

		// mock TaxPolicy
        taxPolicyMock = Mockito.mock(TaxPolicy.class);
        Tax tax = new Tax(Money.ZERO, "");
        Mockito.when(taxPolicyMock.calculateTax(Mockito.any(ProductType.class), Mockito.any(Money.class))).thenReturn(tax);
	}

	// testy stanu
	@Test
	public void zadanieWydaniaFakturyZJednaPozycjaPowinnoZwrocicFaktureZJednaPozycja() {
		InvoiceRequest request = InvoiceRequestBuilder.builder().withProductsCount(1).build();

		Invoice invoice = bookKeeper.issuance(request, taxPolicyMock);

		assertEquals(1, invoice.getItems().size());
	}

	@Test
	public void zadanieWydaniaFakturyBezPozycjiPowinnoZwrocicFuktureBezPozycji() {
		InvoiceRequest req = InvoiceRequestBuilder.builder().build();
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals(0, invoice.getItems().size());
	}

	@Test
	public void fakturaWykonanaDlaDanegoKlientaFaktyczniePosiadaDaneTegoKlienta() {
		InvoiceRequest req = InvoiceRequestBuilder
				.builder()
				.withClient(new ClientData(new Id("007"), "James Bond"))
				.build();
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals("007", invoice.getClient().getAggregateId().toString());
		assertEquals("James Bond", invoice.getClient().getName());
	}

	// testy zachowania
	@Test
	public void zadanieWydaniaFakturyZDwiemaPozycjamiPowinnoWywolacMetodeCalculateTaxDwaRazy() {
		InvoiceRequest req = InvoiceRequestBuilder
				.builder()
				.withProductsCount(2)
				.build();
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals(2, invoice.getItems().size());
		Mockito.verify(taxPolicyMock, Mockito.times(2)).calculateTax(Mockito.any(), Mockito.any());
	}

	@Test
	public void zadanieWydaniaFakturyBezPozycjiPowinnoWywolacMetodeCalculateTaxZeroRazy() {
		InvoiceRequest req = InvoiceRequestBuilder.builder().build();
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals(0, invoice.getItems().size());
		Mockito.verify(taxPolicyMock, Mockito.times(0)).calculateTax(Mockito.any(), Mockito.any());
	}

	@Test
	public void wykonanieFakturyWywolujeMetodeCreateKlasyInvoiceFactoryDokladnieRaz() {
		InvoiceFactory invoiceFactoryMock = Mockito.mock(InvoiceFactory.class);
		BookKeeper bookKeeper = new BookKeeper(invoiceFactoryMock);

		InvoiceRequest req = InvoiceRequestBuilder.builder().build();
		bookKeeper.issuance(req, taxPolicyMock);

		Mockito.verify(invoiceFactoryMock, Mockito.times(1)).create(Mockito.any());
	}
}