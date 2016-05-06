/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.collection.entity;

// Generated 10 Sep, 2009 12:59:27 PM by Hibernate Tools 3.2.0.CR1

import org.egov.commons.Accountdetailkey;
import org.egov.commons.Accountdetailtype;

import java.math.BigDecimal;

/**
 * ReceiptDetail generated by hbm2java
 */
public class AccountPayeeDetail implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private ReceiptDetail receiptDetail;
    private Accountdetailtype accountDetailType;
    private Accountdetailkey accountDetailKey;
    private BigDecimal amount;

    public AccountPayeeDetail() {
    }

    public AccountPayeeDetail(final Accountdetailtype accountDetailType, final Accountdetailkey accountDetailKey,
            final BigDecimal amount, final ReceiptDetail receiptDetail) {
        this.accountDetailKey = accountDetailKey;
        this.accountDetailType = accountDetailType;
        this.amount = amount;
        this.receiptDetail = receiptDetail;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public ReceiptDetail getReceiptDetail() {
        return receiptDetail;
    }

    public void setReceiptDetail(final ReceiptDetail receiptDetail) {
        this.receiptDetail = receiptDetail;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public Accountdetailtype getAccountDetailType() {
        return accountDetailType;
    }

    public void setAccountDetailType(final Accountdetailtype accountDetailType) {
        this.accountDetailType = accountDetailType;
    }

    /**
     * @return the accountDetailKey
     */
    public Accountdetailkey getAccountDetailKey() {
        return accountDetailKey;
    }

    /**
     * @param accountDetailKey
     *            the accountDetailKey to set
     */
    public void setAccountDetailKey(final Accountdetailkey accountDetailKey) {
        this.accountDetailKey = accountDetailKey;
    }
}
