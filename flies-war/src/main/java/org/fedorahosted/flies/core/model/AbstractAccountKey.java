package org.fedorahosted.flies.core.model;

import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;

@MappedSuperclass
public class AbstractAccountKey{
	
	private String keyHash;
	private Account account;

	@NotEmpty
	@Length(min=32, max=32)
	@Id
	public String getKeyHash() {
		return keyHash;
	}
	
	public void setKeyHash(String keyHash) {
		this.keyHash = keyHash;
	}
	
	@NaturalId
	@OneToOne(optional=false, fetch=FetchType.EAGER)
	@JoinColumn(name = "accountId")
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}

}
