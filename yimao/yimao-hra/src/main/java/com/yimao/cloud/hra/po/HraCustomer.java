package com.yimao.cloud.hra.po;

import com.yimao.cloud.pojo.dto.hra.HraCustomerDTO;
import lombok.Data;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Zhang Bo
 * @date 2017/12/6.
 */
@Table(name = "hra_customer")
@Data
public class HraCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String sex;
    private String birthdate;
    private String address;
    private String idcard;
    private String age;
    private String phone;
    private String workaddress;
    private String lifestyle;
    private String race;//种族
    private String bloodtype;
    private String rhfactor;
    private String profession;
    private String height;
    private String weight;
    private String systolicpressure;
    private String diastolicpressure;

    public HraCustomer() {
    }

    /**
     * 用业务对象HraCustomerDTO初始化数据库对象HraCustomer。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public HraCustomer(HraCustomerDTO dto) {
        this.id = dto.getId();
        this.username = dto.getUsername();
        this.sex = dto.getSex();
        this.birthdate = dto.getBirthdate();
        this.address = dto.getAddress();
        this.idcard = dto.getIdcard();
        this.age = dto.getAge();
        this.phone = dto.getPhone();
        this.workaddress = dto.getWorkaddress();
        this.lifestyle = dto.getLifestyle();
        this.race = dto.getRace();
        this.bloodtype = dto.getBloodtype();
        this.rhfactor = dto.getRhfactor();
        this.profession = dto.getProfession();
        this.height = dto.getHeight();
        this.weight = dto.getWeight();
        this.systolicpressure = dto.getSystolicpressure();
        this.diastolicpressure = dto.getDiastolicpressure();
    }

    /**
     * 将数据库实体对象信息拷贝到业务对象HraCustomerDTO上。
     * plugin author ylfjm.
     *
     * @param dto 业务对象
     */
    public void convert(HraCustomerDTO dto) {
        dto.setId(this.id);
        dto.setUsername(this.username);
        dto.setSex(this.sex);
        dto.setBirthdate(this.birthdate);
        dto.setAddress(this.address);
        dto.setIdcard(this.idcard);
        dto.setAge(this.age);
        dto.setPhone(this.phone);
        dto.setWorkaddress(this.workaddress);
        dto.setLifestyle(this.lifestyle);
        dto.setRace(this.race);
        dto.setBloodtype(this.bloodtype);
        dto.setRhfactor(this.rhfactor);
        dto.setProfession(this.profession);
        dto.setHeight(this.height);
        dto.setWeight(this.weight);
        dto.setSystolicpressure(this.systolicpressure);
        dto.setDiastolicpressure(this.diastolicpressure);
    }
}
