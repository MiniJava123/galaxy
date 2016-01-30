package com.dianping.data.warehouse.starshuttle.service.impl;

import com.dianping.data.warehouse.core.common.Const;
import com.dianping.data.warehouse.domain.model.WormholeDO;
import com.dianping.data.warehouse.starshuttle.service.WormholeConverter;
import com.dianping.data.warehouse.starshuttle.utils.JacksonHelper;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by hongdi.tang on 14-3-4.
 */
@Service("XMLConverterImpl")
public class XMLConverterImpl implements WormholeConverter {
    @Override
    public Document convert(List<WormholeDO> wormholes) {
        Document doc = DocumentHelper.createDocument();
        Element job = doc.addElement("job");

        for (WormholeDO para : wormholes){
            Map<String, String> parameterMap = JacksonHelper.jsonToPojo(para.getParameterMapStr(), Map.class);
            if(para.getType().equals(Const.WORMHOLE_READER_TYPE)){
                Element reader = job.addElement(Const.WORMHOLE_READER_TYPE);
                for(Map.Entry<String,String> entry : parameterMap.entrySet()){
                    Element element = reader.addElement(entry.getKey());
                    String text = null;
                    if(entry.getValue() instanceof String){
                        text = StringUtils.isBlank(entry.getValue()) ? "" : entry.getValue();
                    }else{
                        text = entry.getValue() ==null ? "": String.valueOf(entry.getValue());
                    }
                    element.setText(text);
                }
            } else if(para.getType().equals(Const.WORMHOLE_WRITER_TYPE)){
                Element writer = job.addElement(Const.WORMHOLE_WRITER_TYPE);
                for(Map.Entry<String,String> entry : parameterMap.entrySet()){
                    Element element = writer.addElement(entry.getKey());
                    String text = null;
                    if(entry.getValue() instanceof String){
                        text = StringUtils.isBlank(entry.getValue()) ? "" : entry.getValue();
                    }else{
                        text = entry.getValue() ==null ? "": String.valueOf(entry.getValue());
                    }
                    element.setText(text);
                }
            }
        }
        return doc;
    }
}
