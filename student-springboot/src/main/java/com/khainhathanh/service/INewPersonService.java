package com.khainhathanh.service;

import java.util.List;

import com.khainhathanh.dto.NewPersonDTO;

public interface INewPersonService {
	public NewPersonDTO save(NewPersonDTO personDTO);
	public NewPersonDTO update(NewPersonDTO personDTO);
	public void delete(long[] ids);
	public List<NewPersonDTO> listPerson(String name);
}
