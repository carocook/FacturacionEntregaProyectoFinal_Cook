package com.coderhouse.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaRequestDTO {
    private ClienteDTO cliente;
    private List<ItemFacturaDTO> lineas;
}
