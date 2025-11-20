package de.adesso.dataspaces.domaln;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CredentialRequestMessage {

  String credentialType;
  String recipientDid;
}
