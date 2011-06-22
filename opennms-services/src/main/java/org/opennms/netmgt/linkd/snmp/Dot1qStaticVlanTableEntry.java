/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2011 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2011 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.linkd.snmp;

import org.opennms.netmgt.capsd.snmp.NamedSnmpVar;
import org.opennms.netmgt.capsd.snmp.SnmpTableEntry;
import org.opennms.netmgt.snmp.SnmpObjId;
import org.opennms.netmgt.snmp.SnmpResult;
import org.opennms.netmgt.snmp.SnmpUtils;

/**
 *<P>The Dot1qStaticVlanTableEntry class is designed to hold all the MIB-II
 * information for one entry in the
 * .iso.org.dod.internet.mgmt.mib-2.
 * dot1dBridge.qBridgeMIB.qBridgeMIBObjects.dot1qVlan.dot1qVlanCurrentTable
 * <P>This object is used by the Dot1qStaticVlanTable to hold infomation
 * single entries in the table. See the Dot1qStaticVlanTable documentation
 * form more information.</P>
 *
 * @author <A HREF="mailto:rssntn67@yahoo.it">Antonio</A>
 * @see Dot1qStaticVlanTable
 * @see <A HREF="http://www.ietf.org/rfc/rfc1213.txt">RFC1213</A>
 * @version $Id: $
 */
public final class Dot1qStaticVlanTableEntry extends SnmpTableEntry
implements VlanCollectorEntry {

	// Lookup strings for specific table entries
	//
	/** Constant <code>VLAN_STATICEGRESSPORTS="dot1qVlanStaticEgressPorts"</code> */
	public final static String VLAN_STATICEGRESSPORTS = "dot1qVlanStaticEgressPorts";
	/** Constant <code>VLAN_FORBIDDENEGRESSPORTS="dot1qVlanStaticForbiddenEgressPorts"</code> */
	public final static String VLAN_FORBIDDENEGRESSPORTS = "dot1qVlanStaticForbiddenEgressPorts";
	/** Constant <code>VLAN_STATICUNTAGGEDPORTS="dot1qVlanStaticUntaggedPorts"</code> */
	public final static String VLAN_STATICUNTAGGEDPORTS = "dot1qVlanStaticUntaggedPorts";

	private static String VLAN_INDEX_OID=".1.3.6.1.2.1.17.7.1.4.3.1.0";
	private static String VLAN_NAME_OID=".1.3.6.1.2.1.17.7.1.4.3.1.1";
	
	private boolean hasVlanIndex = false;

	/**
	 * <P>The keys that will be supported by default from the 
	 * TreeMap base class. Each of the elements in the list
	 * are an instance of the dot1dbasetable. Objects
	 * in this list should be used by multiple instances of
	 * this class.</P>
	 */
	public static NamedSnmpVar[] hpVlan_elemList = null;

	/**
	 * <P>Initialize the element list for the class. This
	 * is class wide data, but will be used by each instance.</P>
	 */
	static {
		hpVlan_elemList = new NamedSnmpVar[6];
		int ndx = 0;

		hpVlan_elemList[ndx++] = new NamedSnmpVar(NamedSnmpVar.SNMPINT32,
				VLAN_INDEX,VLAN_INDEX_OID,1);

		hpVlan_elemList[ndx++] = new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING,
				VLAN_NAME,VLAN_NAME_OID,2);

		hpVlan_elemList[ndx++] = new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING,
				VLAN_STATICEGRESSPORTS, ".1.3.6.1.2.1.17.7.1.4.3.1.2", 4);
		
		hpVlan_elemList[ndx++] = new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING,
				VLAN_FORBIDDENEGRESSPORTS, ".1.3.6.1.2.1.17.7.1.4.3.1.3", 5);
		
		hpVlan_elemList[ndx++] = new NamedSnmpVar(NamedSnmpVar.SNMPOCTETSTRING,
				VLAN_STATICUNTAGGEDPORTS, ".1.3.6.1.2.1.17.7.1.4.3.1.4", 6);
		
		hpVlan_elemList[ndx++] = new NamedSnmpVar(NamedSnmpVar.SNMPINT32,
				VLAN_STATUS, ".1.3.6.1.2.1.17.7.1.4.3.1.5", 5);


	}

	/**
	 * <P>The TABLE_OID is the object identifier that represents
	 * the root of the table dot1qVlanCurrentTable in the MIB forest.</P>
	 */
	public static final String TABLE_OID = ".1.3.6.1.2.1.17.7.1.4.3.1"; // start of table (GETNEXT)

	/**
	 * <P>The class constructor used to initialize the
	 * object to its initial state. Although the
	 * object's member variables can change after an
	 * instance is created, this constructor will
	 * initialize all the variables as per their named
	 * variable from the passed array of SNMP varbinds.</P>
	 *
	 * <P>If the information in the object should not be
	 * modified then a <EM>final</EM> modifier can be
	 * applied to the created object.</P>
	 */
	public Dot1qStaticVlanTableEntry() {
		super(hpVlan_elemList);
	}

	/** {@inheritDoc} */
	@Override
	public void storeResult(SnmpResult res) {
		if (!hasVlanIndex) {
			int vlanid = res.getInstance().getLastSubId();
			super.storeResult(new SnmpResult(SnmpObjId.get(VLAN_INDEX_OID), res.getInstance(), 
						SnmpUtils.getValueFactory().getInt32(vlanid)));
			hasVlanIndex = true;
		}
		super.storeResult(res);
	}
}
