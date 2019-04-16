package pl.com.bottega.ecommerce.sales.domain.invoicing;


import com.sun.security.ntlm.Client;
import jdk.internal.org.objectweb.asm.tree.InnerClassNode;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {
    private InvoiceRequest invoiceRequest;
    private BookKeeper keeper;

    @Mock
    private TaxPolicy taxPolicy;



    @Before
    public void setUp() {
        ClientData client = new ClientData(Id.generate(), "Filip");
        keeper = new BookKeeper(new InvoiceFactory());
        invoiceRequest = new InvoiceRequest(client);
        when(taxPolicy.calculateTax(ProductType.FOOD, Money.ZERO)).thenReturn(new Tax(Money.ZERO, "food"));
        when(taxPolicy.calculateTax(ProductType.STANDARD, Money.ZERO)).thenReturn(new Tax(Money.ZERO, "standard"));
    }

    @Test
    public void  givenInvoiceRequestWithSingleItem(){
        ProductData productData = new ProductData(Id.generate(), Money.ZERO, "Product", ProductType.FOOD, new Date());
        RequestItem item = new RequestItem(productData,1,Money.ZERO);
        invoiceRequest.add(item);
        Invoice invoice = keeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getItems().size(),is(1));


    }

    @Test
    public void  givenInvoiceRequestWithTwoItem(){
        ProductData productData = new ProductData(Id.generate(), Money.ZERO, "Product", ProductType.FOOD, new Date());
        ProductData productData1 = new ProductData(Id.generate(), Money.ZERO, "Product", ProductType.STANDARD, new Date());
        RequestItem item = new RequestItem(productData,1,Money.ZERO);
        RequestItem item1 = new RequestItem(productData1,1,Money.ZERO);
        invoiceRequest.add(item);
        invoiceRequest.add(item1);
        keeper.issuance(invoiceRequest, taxPolicy);
        verify(taxPolicy, times(1)).calculateTax(ProductType.FOOD, Money.ZERO);
        verify(taxPolicy, times(1)).calculateTax(ProductType.STANDARD, Money.ZERO);

    }

    @Test
    public void invoiceRequestWithoutItemsBeEmpty() {
        Invoice invoice = keeper.issuance(invoiceRequest, taxPolicy);
        invoice.getGros();
        Assert.assertThat(invoice.getItems().size(),is(0));
    }

    @Test
    public void invoiceRequestWithoutItemsNotCallTaxPolicy() {
        keeper.issuance(invoiceRequest, taxPolicy);
        verifyZeroInteractions(taxPolicy);
    }





}