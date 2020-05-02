package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookKeeperTest {

	// testy stanu
	@Test
	public void zadanieWydaniaFakturyZJednaPozycjaPowinnoZwrocicFaktureZJednaPozycja() {
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = TaxPolicyMockBuilder.builder().build();

		InvoiceRequest request = InvoiceRequestMockBuilder.builder().withProductsCount(1).build();

		Invoice invoice = bookKeeper.issuance(request, taxPolicyMock);

		assertEquals(1, invoice.getItems().size());
	}

	@Test
	public void zadanieWydaniaFakturyBezPozycjiPowinnoZwrocicFuktureBezPozycji() {
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = TaxPolicyMockBuilder.builder().build();

		InvoiceRequest req = InvoiceRequestMockBuilder.builder().build();
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals(0, invoice.getItems().size());
	}

	@Test
	public void fakturaWykonanaDlaDanegoKlientaFaktyczniePosiadaDaneTegoKlienta() {
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = TaxPolicyMockBuilder.builder().build();
		InvoiceRequest req = InvoiceRequestMockBuilder
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
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = TaxPolicyMockBuilder.builder().build();

		InvoiceRequest req = InvoiceRequestMockBuilder
				.builder()
				.withProductsCount(2)
				.build();
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals(2, invoice.getItems().size());
		Mockito.verify(taxPolicyMock, Mockito.times(2)).calculateTax(Mockito.any(), Mockito.any());
	}

	@Test
	public void zadanieWydaniaFakturyBezPozycjiPowinnoWywolacMetodeCalculateTaxZeroRazy() {
		BookKeeper bookKeeper = new BookKeeper(new InvoiceFactory());
		TaxPolicy taxPolicyMock = TaxPolicyMockBuilder.builder().build();

		InvoiceRequest req = InvoiceRequestMockBuilder.builder().build();
		Invoice invoice = bookKeeper.issuance(req, taxPolicyMock);

		assertEquals(0, invoice.getItems().size());
		Mockito.verify(taxPolicyMock, Mockito.times(0)).calculateTax(Mockito.any(), Mockito.any());
	}

	@Test
	public void wykonanieFakturyWywolujeMetodeCreateKlasyInvoiceFactoryDokladnieRaz() {
		InvoiceFactory invoiceFactoryMock = Mockito.mock(InvoiceFactory.class);
		BookKeeper bookKeeper = new BookKeeper(invoiceFactoryMock);

		TaxPolicy taxPolicyMock = TaxPolicyMockBuilder.builder().build();
		InvoiceRequest req = InvoiceRequestMockBuilder.builder().build();
		bookKeeper.issuance(req, taxPolicyMock);

		Mockito.verify(invoiceFactoryMock, Mockito.times(1)).create(Mockito.any());
	}
}