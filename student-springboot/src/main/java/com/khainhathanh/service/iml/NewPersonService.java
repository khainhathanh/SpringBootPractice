package com.khainhathanh.service.iml;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khainhathanh.dto.NewPersonDTO;
import com.khainhathanh.entity.PersonEntity;
import com.khainhathanh.repository.NewPersonRepository;
import com.khainhathanh.service.INewPersonService;

@Service
public class NewPersonService implements INewPersonService{

	@Autowired
	NewPersonRepository personRepository;
	
	@Override
	public NewPersonDTO save(NewPersonDTO personDTO) {
		PersonEntity personEntity = this.toEntityPerson(personDTO);
		personEntity = personRepository.save(personEntity);
		return this.toPersonDTO(personEntity);
	}
	
	public PersonEntity toEntityPerson(NewPersonDTO personDTO) {
		PersonEntity personEntity = new PersonEntity();
		personEntity.setFirstName(personDTO.getFirstName());
		personEntity.setLastName(personDTO.getLastName());
		personEntity.setAge(personDTO.getAge());
		personEntity.setDateofbirth(personDTO.getDateofbirth());
		personEntity.setEmail(personDTO.getEmail());
		personEntity.setLanguages(personDTO.getLanguages());
		personEntity.setPhone(personDTO.getPhone());
		personEntity.setSex(personDTO.getPhone());
		return personEntity;
	}
	
	public NewPersonDTO toPersonDTO(PersonEntity personEntity) {
		NewPersonDTO personDTO = new NewPersonDTO();
		personDTO.setFirstName(personEntity.getFirstName());
		personDTO.setLastName(personEntity.getLastName());
		personDTO.setAge(personEntity.getAge());
		personDTO.setDateofbirth(personEntity.getDateofbirth());
		personDTO.setEmail(personEntity.getEmail());
		personDTO.setLanguages(personEntity.getLanguages());
		personDTO.setPhone(personEntity.getPhone());
		personEntity.setSex(personEntity.getPhone());
		return personDTO;
	}
	
	public PersonEntity updateEntityPerson(NewPersonDTO personDTO, PersonEntity personEntity) {
		personEntity.setFirstName(personDTO.getFirstName());
		personEntity.setLastName(personDTO.getLastName());
		personEntity.setAge(personDTO.getAge());
		personEntity.setDateofbirth(personDTO.getDateofbirth());
		personEntity.setEmail(personDTO.getEmail());
		personEntity.setLanguages(personDTO.getLanguages());
		personEntity.setPhone(personDTO.getPhone());
		personEntity.setSex(personDTO.getPhone());
		return personEntity;
	}

	@Override
	public NewPersonDTO update(NewPersonDTO personDTO) {
		PersonEntity oldPersonEntity = personRepository.findOne(personDTO.getId());
		oldPersonEntity = this.updateEntityPerson(personDTO, oldPersonEntity);
		oldPersonEntity = personRepository.save(oldPersonEntity);
		return this.toPersonDTO(oldPersonEntity);
	}

	@Override
	public void delete(long[] ids) {
		for(long item: ids) {
			personRepository.delete(item);
		}	
	}

	@Override
	public List<NewPersonDTO> listPerson(String name) {
		List<PersonEntity> listPeronE = personRepository.findByLastName(name);
		List<NewPersonDTO> listPerDTO = new ArrayList<>();
		for(PersonEntity item: listPeronE) {
			NewPersonDTO perDTO = new NewPersonDTO();
			perDTO = this.toPersonDTO(item);
			listPerDTO.add(perDTO);
		}
		return listPerDTO;
	}
}
