package pl.com.bottega.ecommerce.sales.domain.invoicing;


import com.sun.security.ntlm.Client;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {
    private InvoiceRequest invoiceRequest;
    private BookKeeper keeper;
    @Mock
    private TaxPolicy taxPolicy;

    @Before
    public void setUp() {
        ClientData client = new ClientData(Id.generate(), "Filip");
        InvoiceRequest invoiceRequest = new InvoiceRequest(client);
        when(taxPolicy.calculateTax(ProductType.FOOD, Money.ZERO));
    }
    @Test
    public void  givenInvoiceRequestWithSingleItem(){
        ProductData productData = new ProductData(Id.generate(), Money.ZERO,"SD", ProductType.FOOD,new Date());
        RequestItem item = new RequestItem(productData,1,Money.ZERO);
        Invoice invoice = new Invoice();


    }






}