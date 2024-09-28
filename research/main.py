import torch
import torch.nn as nn
import torch.nn.functional as F
import ai_edge_torch

import numpy
import torchvision
import os


class SampleModel(nn.Module):
    def __init__(self):
        super().__init__()
        # self.conv1 = nn.Conv2d(1, 20, 5)
        # self.conv2 = nn.Conv2d(20, 20, 5)
        self.weights = nn.Parameter(torch.tensor([2.0]), requires_grad=True)
        self.weights_2 = nn.Parameter(torch.tensor([2.0]), requires_grad=True)

    def forward(self, x):
        # x = F.relu(self.conv1(x))
        # return F.relu(self.conv2(x))
        a = self.weights * x
        a_1 = a.reshape(1, -1)
        b = a_1.sum(dim=-1)
        c = b.reshape(-1) * self.weights_2
        return c


def export_resnet():
    os.environ["PJRT_DEVICE"] = "CPU"

    resnet18 = torchvision.models.resnet18(
        torchvision.models.ResNet18_Weights.IMAGENET1K_V1
    ).eval()
    sample_input = (torch.randn(1, 3, 224, 224),)
    sample_output = resnet18(*sample_input)

    edge_model = ai_edge_torch.convert(resnet18.eval(), sample_input)
    output = edge_model(*sample_input)
    output = torch.from_numpy(output)

    print(
        f"Model exported correctly: {torch.isclose(output, sample_output.detach(), atol=1e-5).all()}"
    )
    edge_model.export("resnet.tflite")

    print("Done")


if __name__ == "__main__":
    os.environ["PJRT_DEVICE"] = "CPU"

    model = SampleModel().eval()
    fake_input = torch.tensor([1.0, 2.0, 3.0])

    expected_output = model(fake_input)
    # print(fake_output)

    sample_input = (fake_input,)
    edge_model = ai_edge_torch.convert(model.eval(), sample_input)
    output = edge_model(*sample_input)
    output = torch.from_numpy(output)

    print("-- Success -- ")
    print(expected_output)
    print(output)
    print(torch.isclose(expected_output, output, atol=1e-5).all())

    edge_model.export("model.tflite")

    print("Done")
