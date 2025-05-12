package com.example.spring_batch_chunk.reader;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.ParseException;

import com.example.spring_batch_chunk.model.DataEntity;

public class ItemReaderStep implements ItemReader<DataEntity>{

   	private static final Logger logger = LoggerFactory.getLogger(ItemReaderStep.class);
  
    private List<DataEntity> data;
	
	public ItemReaderStep(List<DataEntity> data) {
		this.data = data;
		logger.info("~~~~ ItemReaderStep created with {} item to read ~~~~", this.data.size());
	}
	
	@Override
	public DataEntity read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		try {
			
            if (!getData().isEmpty()) {
				return getData().remove(0);
			}

		} catch (Exception ex) {
			logger.error("Fatal Error on reading data", ex);
		}
	
		return null;
	}
	
	public List<DataEntity> getData() {
		if (this.data == null) {
			this.data = new ArrayList<>();
		}
		return data;
	}
}
