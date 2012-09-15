/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mobicents.media.server.impl.rtp;

import org.mobicents.media.server.spi.ConnectionMode;
import org.mobicents.media.server.spi.format.Formats;
import org.mobicents.media.server.spi.format.AudioFormat;
import org.mobicents.media.server.component.DspFactoryImpl;
import org.mobicents.media.server.component.Dsp;
import org.mobicents.media.server.impl.rtp.sdp.AVProfile;
import java.net.InetSocketAddress;
import org.mobicents.media.server.io.ss7.SS7Manager;
import org.mobicents.media.server.component.audio.CompoundComponent;
import org.mobicents.media.server.component.audio.CompoundMixer;
import org.mobicents.media.server.component.audio.Sine;
import org.mobicents.media.server.component.audio.SpectraAnalyzer;
import org.mobicents.media.server.scheduler.DefaultClock;
import org.mobicents.media.server.io.network.UdpManager;
import org.mobicents.media.server.scheduler.Scheduler;
import org.mobicents.media.server.scheduler.Clock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mobicents.media.server.spi.format.FormatFactory;
import static org.junit.Assert.*;

/**
 *
 * @author oifa yulian
 */
public class LocalChannelTest {

    //clock and scheduler
    private Clock clock;
    private Scheduler scheduler;

    private ChannelsManager channelsManager;
    private UdpManager udpManager;

    private SpectraAnalyzer analyzer1,analyzer2;
    private Sine source1,source2;

    private LocalDataChannel channel1,channel2;
    
    private int fcount;

    private DspFactoryImpl dspFactory = new DspFactoryImpl();
    
    private CompoundMixer compoundMixer1,compoundMixer2;
    private CompoundComponent component1,component2;
    
    public LocalChannelTest() {
    }

    @Before
    public void setUp() throws Exception {
    	//use default clock
        clock = new DefaultClock();

        //create single thread scheduler
        scheduler = new Scheduler();
        scheduler.setClock(clock);
        scheduler.start();

        udpManager = new UdpManager(scheduler);
        udpManager.start();

        channelsManager = new ChannelsManager(udpManager);
        channelsManager.setScheduler(scheduler);

        source1 = new Sine(scheduler);
        source1.setFrequency(50);        
        
        source2 = new Sine(scheduler);
        source2.setFrequency(100);
        
        analyzer1 = new SpectraAnalyzer("analyzer",scheduler);        
        analyzer2 = new SpectraAnalyzer("analyzer",scheduler);
        
        channel1 = channelsManager.getLocalChannel();
        channel2 = channelsManager.getLocalChannel();
        channel1.join(channel2);
        
        compoundMixer1=new CompoundMixer(scheduler);
        compoundMixer2=new CompoundMixer(scheduler);
        
        component1=new CompoundComponent(1);
        component1.addInput(source1.getCompoundInput());
        component1.addOutput(analyzer1.getCompoundOutput());
        component1.updateMode(true,true);
        
        compoundMixer1.addComponent(component1);
        compoundMixer1.addComponent(channel1.getCompoundComponent());
        
        component2=new CompoundComponent(2);
        component2.addInput(source2.getCompoundInput());
        component2.addOutput(analyzer2.getCompoundOutput());
        component2.updateMode(true,true);
        
        compoundMixer2.addComponent(component2);
        compoundMixer2.addComponent(channel2.getCompoundComponent());        
    }

    @After
    public void tearDown() {
    	source1.deactivate();
    	source2.deactivate();
    	analyzer1.deactivate();
    	analyzer2.deactivate();
    	
    	channel1.unjoin();    	    	
    	channel2.unjoin();
    	
    	compoundMixer1.stop();
    	compoundMixer2.stop();
    	
        udpManager.stop();
        scheduler.stop();
    }

    @Test
    public void testTransmission() throws Exception {
    	channel1.updateMode(ConnectionMode.SEND_RECV);
    	channel2.updateMode(ConnectionMode.SEND_RECV);
    	
        source1.activate();
        source2.activate();
        analyzer1.activate();
        analyzer2.activate();
    	compoundMixer1.start();
    	compoundMixer2.start();
        
        Thread.sleep(5000);
        
        analyzer1.deactivate();
        analyzer2.deactivate();
        source1.deactivate();
        source2.deactivate();
        compoundMixer1.stop();        
        compoundMixer2.stop();
        channel1.updateMode(ConnectionMode.INACTIVE);
        
        int s1[] = analyzer1.getSpectra();
        int s2[] = analyzer2.getSpectra();
        
        if (s1.length != 1 || s2.length != 1) {
            System.out.println("Failure ,s1:" + s1.length + ",s2:" + s2.length);
            fcount++;
        } else System.out.println("Passed");

        assertEquals(1, s1.length);    	
        assertEquals(1, s2.length);
        
        assertEquals(100, s1[0], 5);
        assertEquals(50, s2[0], 5);
    }
}