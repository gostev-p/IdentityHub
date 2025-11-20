package de.adesso.dataspaces.controller;

import de.adesso.dataspaces.service.IssuanceService;


public class IssuanceApiController {

  private final IssuanceService issuanceService;

  public IssuanceApiController(IssuanceService issuanceService) {
    this.issuanceService = issuanceService;
  }
}
