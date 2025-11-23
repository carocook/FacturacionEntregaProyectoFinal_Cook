package com.coderhouse.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaRequestDTO {
    private String numero;
    private Long clienteId;
    private List<ItemFacturaDTO> items;
}
