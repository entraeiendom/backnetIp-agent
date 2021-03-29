package no.entra.bacnet.agent.commands.properties;


import no.entra.bacnet.apdu.Apdu;
import no.entra.bacnet.apdu.SDContextTag;
import no.entra.bacnet.bvlc.Bvlc;
import no.entra.bacnet.bvlc.BvlcBuilder;
import no.entra.bacnet.bvlc.BvlcFunction;
import no.entra.bacnet.json.services.ConfirmedServiceChoice;
import no.entra.bacnet.npdu.Npdu;
import no.entra.bacnet.npdu.NpduBuilder;
import no.entra.bacnet.objects.ObjectId;
import no.entra.bacnet.objects.PduType;
import no.entra.bacnet.objects.PropertyIdentifier;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static no.entra.bacnet.utils.HexUtils.intToHexString;

public class RPMCommand {
    private final InetAddress sendToAddress;
    private final int invokeId;
    private final ObjectId objectId;
    private Boolean confirmedNotifications = null;
    private Exception error = null;
    private final List<PropertyIdentifier> propertyIdentifiers;

    public RPMCommand(InetAddress sendToAddress, ObjectId objectId) {
        this.sendToAddress = sendToAddress;
        this.objectId = objectId;
        invokeId = 0;
        propertyIdentifiers = new ArrayList<>();
    }

    public RPMCommand(InetAddress sendToAddress, int invokeId, ObjectId objectId, List<PropertyIdentifier> propertyIdentifiers) {
        this.sendToAddress = sendToAddress;
        this.invokeId = invokeId;
        this.objectId = objectId;
        this.propertyIdentifiers = propertyIdentifiers;
    }

    protected String buildHexString() {
        Apdu apdu = Apdu.ApduBuilder.builder()
                .withApduType(PduType.ConfirmedRequest)
                .isSegmented(false)
                .hasMoreSegments(false)
                .isSegmentedReplyAllowed(true)
                .withMaxSegmentsAcceptedAbove64()
                .withMaxApduLength1476()
                .build();
        String apduHexString = apdu.toHexString()
                + intToHexString(invokeId,2)
                + ConfirmedServiceChoice.ReadPropertyMultiple.getServiceChoiceHex()
                + SDContextTag.TAG0LENGTH4 + objectId.toHexString();
        //Add ContextTag0, DeviceId
//        apdu += ApduType  //SDContextTag.TAG0LENGTH4 + objectId.toHexString();
        Bvlc bvlc = new BvlcBuilder(BvlcFunction.OriginalUnicastNpdu).withTotalNumberOfOctets(23).build();
        Npdu npdu = new NpduBuilder().withExpectingReply().build();
        return bvlc.toHexString() + npdu.toHexString() + apduHexString;
    }

    public static final class RPMCommandBuilder {
        private InetAddress sendToAddress;
        private int invokeId;
        private ObjectId objectId;
        private Boolean confirmedNotifications = null;
        private Exception error = null;
        private List<PropertyIdentifier> propertyIdentifiers;

        private RPMCommandBuilder() {
        }

        public RPMCommandBuilder(InetAddress sendToAddress) {
            this.sendToAddress = sendToAddress;
        }

        public static RPMCommandBuilder aRPMCommand() {
            return new RPMCommandBuilder();
        }

        public RPMCommandBuilder withSendToAddress(InetAddress sendToAddress) {
            this.sendToAddress = sendToAddress;
            return this;
        }

        public RPMCommandBuilder withInvokeId(int invokeId) {
            this.invokeId = invokeId;
            return this;
        }

        public RPMCommandBuilder withObjectId(ObjectId objectId) {
            this.objectId = objectId;
            return this;
        }

        public RPMCommandBuilder withConfirmedNotifications(Boolean confirmedNotifications) {
            this.confirmedNotifications = confirmedNotifications;
            return this;
        }

        public RPMCommandBuilder withError(Exception error) {
            this.error = error;
            return this;
        }

        public RPMCommandBuilder withPropertyIdentifiers(List<PropertyIdentifier> propertyIdentifiers) {
            this.propertyIdentifiers = propertyIdentifiers;
            return this;
        }
        public RPMCommandBuilder withPropertyIdentifier(PropertyIdentifier propertyIdentifier) {
            if (propertyIdentifiers == null) {
                propertyIdentifiers = new ArrayList<>();
            }
            propertyIdentifiers.add(propertyIdentifier);
            return this;
        }

        public RPMCommand build() {
            RPMCommand rPMCommand = new RPMCommand(sendToAddress, invokeId, objectId, propertyIdentifiers);
            rPMCommand.confirmedNotifications = this.confirmedNotifications;
            rPMCommand.error = this.error;
            return rPMCommand;
        }
    }
}